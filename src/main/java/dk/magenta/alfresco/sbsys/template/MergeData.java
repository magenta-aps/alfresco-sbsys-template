package dk.magenta.alfresco.sbsys.template;

import com.google.gson.Gson;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.util.GUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.wickedsource.docxstamper.DocxStamper;
import org.wickedsource.docxstamper.DocxStamperConfiguration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MergeData extends AbstractWebScript {
    private static Log logger = LogFactory.getLog(MergeData.class);

    private ContentService contentService;
    private FileFolderService fileFolderService;
    private Properties properties;
    private SiteService siteService;

    private static final String AUTHORIZATION = "Authorization";
    private static final String TOKEN = "token";

    // TODO: move this string to common class
    private static final String CONTAINER = "documentLibrary";


    @Override
    public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) throws IOException {

        Gson gson = new Gson();

        //////////////////  Get POSTed JSON as string from request and deserialize into POJO ///////////////////
        TemplateReceiveModel req = gson.fromJson(
                webScriptRequest.getContent().getContent(),
                TemplateReceiveModel.class
        );
        logger.debug(req.token.get(TOKEN));

        ////////////////////// Call SBSYS to get case metadata ////////////////////////////////////

        // SSL
//        SSLContext sslContext = SSLContexts.custom()
//                .loadTrustMaterial(new File())

//        SSLContext sslContext = SSLContexts.createDefault();
//        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
//                sslContext,
//                new String[] {"TLSv1"},
//                null,
//                SSLConnectionSocketFactory.getDefaultHostnameVerifier()
//        );

        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManagerImpl()
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, trustAllCerts, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                sslContext,
                new String[]{"TLSv1.2"},
                null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier()
        );

        // Get case details from the SBSYS server
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build();

        try {
            // TODO: fix hardcoded URL
            HttpGet httpGet = new HttpGet("https://sbsip-m-01.bk-sbsys.dk:28443/convergens-sbsip-sbsys-webapi-proxy/proxy/api/sag/" + req.kladde.get("SagID"));
            httpGet.addHeader(AUTHORIZATION, "Bearer " + req.token.get(TOKEN));
            logger.debug(httpGet.toString());

            // Create response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                @Override
                public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        logger.debug("##### SBSYS server message #####");
                        logger.debug(response.getStatusLine().getReasonPhrase());
                        logger.debug(response.getEntity().getContent().toString());
                        logger.debug("################################");
                        throw new AlfrescoRuntimeException("Got HTTP status " + Integer.toString(status) + " from SBSYS server");
                    }
                }
            };

            String response = httpClient.execute(httpGet, responseHandler);
            logger.debug(response);

            Case sbsysCase = gson.fromJson(response, Case.class);

            ///////////////////////// Get template and merge case data ///////////////////////////

            // The NodeRef should be constructed in a better way
            ContentReader contentReader = contentService.getReader(
                    new NodeRef("workspace://SpacesStore/" + req.id),
                    ContentModel.PROP_CONTENT
            );
            InputStream inputStream = contentReader.getContentInputStream();

            // Get the pre-upload folder
            // TODO: remove magic pre-upload
            PagingRequest pagingRequest = new PagingRequest(Integer.MAX_VALUE);
            PagingResults<FileInfo> containers = siteService.listContainers(getSite(), pagingRequest);
            NodeRef docLib = siteService.getContainer(getSite(), CONTAINER);
            List<FileInfo> docLibFolders = fileFolderService.listFolders(docLib);
            FileInfo preUpload = docLibFolders.stream()
                    .filter((FileInfo fileInfo) -> fileInfo.getName().equals("pre-upload"))
                    .findFirst()
                    .get();

            // Create the merged document
            String preUploadFilename = GUID.generate();
            FileInfo mergedDoc = fileFolderService.create(
                    preUpload.getNodeRef(),
                    preUploadFilename + ".docx",
                    ContentModel.TYPE_CONTENT
            );

            ContentWriter contentWriter = contentService.getWriter(
                    mergedDoc.getNodeRef(),
                    ContentModel.PROP_CONTENT,
                    true
            );
            // TODO: remove magic value
            contentWriter.setMimetype("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            OutputStream outputStream = contentWriter.getContentOutputStream();

            // Merge data into template
            DocxStamper stamper = new DocxStamperConfiguration().build();
            stamper.stamp(inputStream, sbsysCase, outputStream);

            inputStream.close();
            outputStream.close();

            /////////////////////// Build response /////////////////////////
            Map<String, String> resp = new HashMap<>();
            // TODO: remove magic keys/values
            resp.put("preUploadId", mergedDoc.getNodeRef().toString());
            resp.put("preUploadFilename", preUploadFilename);
            resp.put("url", "https://alfrescoskabelon.magenta.dk/share/page/site/swsdp/onlyoffice-edit?nodeRef=" + mergedDoc.getNodeRef().toString());

            String result = gson.toJson(resp);

            try {
                webScriptResponse.getWriter().write(result);
            } catch (IOException e) {
                throw new AlfrescoRuntimeException(e.getMessage());
            }

            System.out.println("hurra");
        } finally {
            // TODO: close the try block earlier
            httpClient.close();
        }


        logger.debug("Working!!!");
    }

    /**
     * Get site short name from alfresco-global.properties
     * @return Site short name
     */
    private String getSite() {
        return properties.getProperty("sbsys.template.site");
    }


    ////////////////////// Getters and setters /////////////////////////

    public ContentService getContentService() {
        return contentService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public FileFolderService getFileFolderService() {
        return fileFolderService;
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public SiteService getSiteService() {
        return siteService;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
}
