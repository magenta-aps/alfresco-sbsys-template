package dk.magenta.alfresco.sbsys.template;

import com.google.gson.JsonSyntaxException;
import dk.magenta.alfresco.sbsys.template.json.Case;
import dk.magenta.alfresco.sbsys.template.json.Merge;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.util.GUID;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.wickedsource.docxstamper.DocxStamper;
import org.wickedsource.docxstamper.DocxStamperConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MergeData extends AbstractWebScript {
    private static Log logger = LogFactory.getLog(MergeData.class);

    private AttributeService attributeService;
    private FileFolderService fileFolderService;
    private NodeRefUtil nodeRefUtil;
    private Properties properties;

    public static final String CASE_ID = "caseId";
    public static final String DOCUMENT_NAME = "documentName";
    public static final String TOKEN = "token";
    public static final String PREUPLOAD_FOLDER = "pre-upload";

    @Override
    public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) {
        logger.debug("Merge webscript called");

        try {

            ////////////////////////// Get request body ///////////////////////////

            // Get POSTed JSON as string from request and deserialize into POJO
            Merge req = RequestResponseHandler.deserialize(
                    webScriptRequest.getContent().getContent(),
                    Merge.class
            );

            // Call SBSYS to get case metadata
            String response = HttpHandler.GET(
                    properties.getProperty("sbsys.template.url.get.case") + "/" + req.getKladde().getSagID(),
                    req.getToken().get(TOKEN)
            );
            logger.debug(response);
            Case sbsysCase = RequestResponseHandler.deserialize(response, Case.class);

            ///////////////////// Merge data into template ////////////////////////

            // Get InputStream for template document
            InputStream inputStream = nodeRefUtil.getInputStream("workspace://SpacesStore/" + req.getId());

            // Get the pre-upload folder
            List<FileInfo> docLibFolders = fileFolderService.listFolders(nodeRefUtil.getDocLib());
            FileInfo preUpload = docLibFolders.stream()
                    .filter((FileInfo fileInfo) -> fileInfo.getName().equals(PREUPLOAD_FOLDER))
                    .findFirst()
                    .get();

            // Create the merged document
            String preUploadFilename = GUID.generate();
            FileInfo mergedDoc = fileFolderService.create(
                    preUpload.getNodeRef(),
                    preUploadFilename + ".docx",
                    ContentModel.TYPE_CONTENT
            );

            // Add versionable aspect
            nodeRefUtil.setVersionableAspect(mergedDoc.getNodeRef());

            // Set the date
            sbsysCase.setDato(nodeRefUtil.getNodeRefCreationDate(mergedDoc));

            OutputStream outputStream = nodeRefUtil.getOutputStream(mergedDoc.getNodeRef());

            // Merge data into template
            DocxStamper stamper = new DocxStamperConfiguration().build();
            stamper.stamp(inputStream, sbsysCase, outputStream);

            inputStream.close();
            outputStream.close();

            ///////////// Store caseId and document name in the AttributeService /////////////

            attributeService.createAttribute(req.getKladde().getSagID(), mergedDoc.getNodeRef().toString(), CASE_ID);
            attributeService.createAttribute(req.getKladde().getNavn(), mergedDoc.getNodeRef().toString(), DOCUMENT_NAME);

            // The full length of the token cannot be stored in one go since it is too long
            attributeService.createAttribute(req.getToken().get(TOKEN).substring(0, 1024), mergedDoc.getNodeRef().toString(), TOKEN + "0");
            attributeService.createAttribute(req.getToken().get(TOKEN).substring(1024, 2048), mergedDoc.getNodeRef().toString(), TOKEN + "1");
            attributeService.createAttribute(req.getToken().get(TOKEN).substring(2048), mergedDoc.getNodeRef().toString(), TOKEN + "2");

            /////////////////////// Build response /////////////////////////

            Map<String, String> resp = new HashMap<>();
            resp.put("preUploadId", mergedDoc.getNodeRef().toString());
            resp.put("preUploadFilename", preUploadFilename + ".docx");
            resp.put("url",
                    properties.getProperty("alfresco.protocol")
                            + "://" + properties.getProperty("alfresco.host")
                            + "/share/page/site/"
                            + properties.getProperty("sbsys.template.site")
                            + "/onlyoffice-edit?nodeRef="
                            + mergedDoc.getNodeRef().toString()
            );

            String json = RequestResponseHandler.serialize(resp);
            logger.debug(json);
            RequestResponseHandler.writeWebscriptResponse(webScriptResponse, json);

        } catch (JsonSyntaxException e) {
            webScriptResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
            RequestResponseHandler.writeWebscriptResponse(
                    webScriptResponse,
                    RequestResponseHandler.getJsonSyntaxErrorMessage()
            );
        } catch (IOException e) {
            e.printStackTrace();
            throw new AlfrescoRuntimeException(e.getMessage());
        }
    }

    ////////////////////// Getters and setters /////////////////////////

    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    public FileFolderService getFileFolderService() {
        return fileFolderService;
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    public void setNodeRefUtil(NodeRefUtil nodeRefUtil) {
        this.nodeRefUtil = nodeRefUtil;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
