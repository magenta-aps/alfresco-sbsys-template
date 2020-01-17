package dk.magenta.alfresco.sbsys.template;

import com.google.gson.JsonSyntaxException;
import dk.magenta.alfresco.sbsys.template.json.Upload;
import dk.magenta.alfresco.sbsys.template.json.MultipartRequest;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Map;

public class UploadDocument extends AbstractWebScript {

    private static Log logger = LogFactory.getLog(UploadDocument.class);

    private AttributeService attributeService;
    private NodeRefUtil nodeRefUtil;

    private static final int OO_DELAY = 10;

    @Override
    public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) {

        try {
            Upload req = RequestResponseHandler.deserialize(
                    webScriptRequest.getContent().getContent(),
                    Upload.class
            );

            String sagId = (String) attributeService.getAttribute(req.getPreUploadId(), MergeData.CASE_ID);
            String documentName = (String) attributeService.getAttribute(req.getPreUploadId(), MergeData.DOCUMENT_NAME);
            Map<String, String> documentDetails = onlyOfficeDelayAndGetDocumentDetails(req.getPreUploadId());

            // NOTE: this is NOT a multipart/form-data request. It is a normal POST request to a
            // separate service that in turn will perform the actual multipart/form-data request

            MultipartRequest multipartRequest = new MultipartRequest(
                    Integer.parseInt(sagId),
                    documentName,
                    documentDetails.get("filename"),
                    documentDetails.get("mimeType"),
                    req.getToken().get("token"),
                    documentDetails.get("contentStorePath")
                    );

            String response = HttpHandler.POST_MULTIPART(multipartRequest);

            logger.debug("Document uploaded");

            nodeRefUtil.deleteNode(req.getPreUploadId());
            attributeService.removeAttribute(req.getPreUploadId(), MergeData.CASE_ID);
            attributeService.removeAttribute(req.getPreUploadId(), MergeData.DOCUMENT_NAME);
            attributeService.removeAttribute(req.getPreUploadId(), MergeData.FILE_PATH);

            logger.debug("Final template document deleted");

        } catch (JsonSyntaxException e) {
            webScriptResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
            RequestResponseHandler.writeWebscriptResponse(
                    webScriptResponse,
                    RequestResponseHandler.getJsonSyntaxErrorMessage()
            );
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new AlfrescoRuntimeException(e.getMessage());
        }
    }

    private Map<String, String> onlyOfficeDelayAndGetDocumentDetails(String preUploadId) throws InterruptedException {
        LocalTime startTime = LocalTime.now();
        String oldFilePath = (String) attributeService.getAttribute(preUploadId, MergeData.FILE_PATH);

        logger.debug("oldFilePath = " + oldFilePath);

        boolean okToUpload = false;
        Map<String, String> documentDetails = null;
        while (!okToUpload) {
            Duration duration = Duration.between(startTime, LocalTime.now());
            documentDetails = nodeRefUtil.getUploadDocumentDetails(preUploadId);
            String newFilePath = documentDetails.get("contentStorePath");
            if (!newFilePath.equals(oldFilePath) || duration.getSeconds() > OO_DELAY) {
                okToUpload = true;
            } else {
                Thread.sleep(1000);
            }

            logger.debug("Duration = " + duration);
            logger.debug("newFilePath = " + newFilePath);
        }

        return documentDetails;
    }

    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    public void setNodeRefUtil(NodeRefUtil nodeRefUtil) {
        this.nodeRefUtil = nodeRefUtil;
    }
}
