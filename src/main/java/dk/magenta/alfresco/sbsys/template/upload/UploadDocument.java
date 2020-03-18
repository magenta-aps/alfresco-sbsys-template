package dk.magenta.alfresco.sbsys.template.upload;

import com.google.gson.JsonSyntaxException;
import dk.magenta.alfresco.sbsys.template.json.requests.Upload403;
import dk.magenta.alfresco.sbsys.template.utils.Constants;
import dk.magenta.alfresco.sbsys.template.http.HttpHandler;
import dk.magenta.alfresco.sbsys.template.utils.NodeRefUtil;
import dk.magenta.alfresco.sbsys.template.http.RequestResponseHandler;
import dk.magenta.alfresco.sbsys.template.json.requests.Upload;
import dk.magenta.alfresco.sbsys.template.json.requests.MultipartRequest;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.util.Map;

import static dk.magenta.alfresco.sbsys.template.utils.Constants.JSON_SYNTAX_ERROR_MSG;

public class UploadDocument extends AbstractWebScript {

    private static Log logger = LogFactory.getLog(UploadDocument.class);

    private AttributeService attributeService;
    private JsonBuilderStrategy jsonBuilderStrategy;
    private CleanUpStrategy cleanUpStrategy;
    private NodeRefUtil nodeRefUtil;
    private Upload req;

    @Override
    public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) {
        logger.debug("Upload webscript called");
        try {
                req = RequestResponseHandler.deserialize(
                    webScriptRequest.getContent().getContent(),
                    Upload.class
            );

            Map<String, String> documentDetails = nodeRefUtil.getUploadDocumentDetails(req.getPreUploadId());

            // Only upload the document if the version > 1.0
            nodeRefUtil.verifyVersionForUpload(req.getPreUploadId());

            // NOTE: this is NOT a multipart/form-data request. It is a normal POST request to a
            // separate service that in turn will perform the actual multipart/form-data request

            MultipartRequest multipartRequest = new MultipartRequest(
                    jsonBuilderStrategy.build(this),
                    documentDetails.get("filename"),
                    documentDetails.get("mimeType"),
                    (String) attributeService.getAttribute(req.getPreUploadId(), Constants.URL),
                    req.getToken().get("token"),
                    documentDetails.get("contentStorePath")
            );
            logger.debug("MultipartRequest: " + RequestResponseHandler.serialize(multipartRequest));

            String response = HttpHandler.POST_MULTIPART(multipartRequest);

            logger.debug("Document uploaded");

            nodeRefUtil.deleteNode(req.getPreUploadId());
            cleanUpStrategy.cleanUp(this);

            logger.debug("Document deleted");

            attributeService.removeAttribute(req.getPreUploadId(), Constants.OPERATION);

            RequestResponseHandler.writeWebscriptResponse(
                    webScriptResponse,
                    RequestResponseHandler.getJsonMessage("success")
            );

        } catch (JsonSyntaxException e) {
            webScriptResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
            RequestResponseHandler.writeWebscriptResponse(
                    webScriptResponse,
                    RequestResponseHandler.getJsonMessage(JSON_SYNTAX_ERROR_MSG)
            );
        } catch (VersionUploadException e) {
            webScriptResponse.setStatus(HttpStatus.SC_FORBIDDEN);
            RequestResponseHandler.writeWebscriptResponse(
                    webScriptResponse,
                    RequestResponseHandler.serialize(new Upload403("Document not yet saved", 1))
            );
        } catch (IOException e) {
            e.printStackTrace();
            throw new AlfrescoRuntimeException(e.getMessage());
        }
    }

    public AttributeService getAttributeService() {
        return attributeService;
    }

    public Upload getReq() {
        return req;
    }

    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    public void setCleanUpStrategy(CleanUpStrategy cleanUpStrategy) {
        this.cleanUpStrategy = cleanUpStrategy;
    }

    public void setJsonBuilderStrategy(JsonBuilderStrategy jsonBuilderStrategy) {
        this.jsonBuilderStrategy = jsonBuilderStrategy;
    }

    public void setNodeRefUtil(NodeRefUtil nodeRefUtil) {
        this.nodeRefUtil = nodeRefUtil;
    }
}
