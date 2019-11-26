package dk.magenta.alfresco.sbsys.template;

import dk.magenta.alfresco.sbsys.template.json.Case;
import dk.magenta.alfresco.sbsys.template.json.TemplateReceiver;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
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

public class MergeData extends AbstractWebScript {
    private static Log logger = LogFactory.getLog(MergeData.class);

    private FileFolderService fileFolderService;
    private NodeRefUtil nodeRefUtil;

    private static final String AUTHORIZATION = "Authorization";
    private static final String TOKEN = "token";

    @Override
    public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) throws IOException {

        //////////////////  Get POSTed JSON as string from request and deserialize into POJO ///////////////////

        TemplateReceiver req = RequestResponseHandler.deserialize(
            webScriptRequest.getContent().getContent(),
            TemplateReceiver.class
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

            Case sbsysCase = RequestResponseHandler.deserialize(response, Case.class);

            ///////////////////////// Get template and merge case data ///////////////////////////

            // The NodeRef should be constructed in a better way
            InputStream inputStream = nodeRefUtil.getInputStream("workspace://SpacesStore/" + req.id);

            // Get the pre-upload folder
            // TODO: remove magic pre-upload
            List<FileInfo> docLibFolders = fileFolderService.listFolders(nodeRefUtil.getDocLib());
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

            OutputStream outputStream = nodeRefUtil.getOutputStream(mergedDoc.getNodeRef());

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

            String json = RequestResponseHandler.serialize(resp);
            RequestResponseHandler.writeWebscriptResponse(webScriptResponse, json);

        } finally {
            // TODO: close the try block earlier
            httpClient.close();
        }
    }

    ////////////////////// Getters and setters /////////////////////////

    public FileFolderService getFileFolderService() {
        return fileFolderService;
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    public void setNodeRefUtil(NodeRefUtil nodeRefUtil) {
        this.nodeRefUtil = nodeRefUtil;
    }
}
