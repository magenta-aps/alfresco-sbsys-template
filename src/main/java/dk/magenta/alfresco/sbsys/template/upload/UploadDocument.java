package dk.magenta.alfresco.sbsys.template.upload;

import com.google.gson.JsonSyntaxException;
import dk.magenta.alfresco.sbsys.template.HttpHandler;
import dk.magenta.alfresco.sbsys.template.NodeRefUtil;
import dk.magenta.alfresco.sbsys.template.RequestResponseHandler;
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
import java.util.Map;

public class UploadDocument extends AbstractWebScript {

    private static Log logger = LogFactory.getLog(UploadDocument.class);

    private AttributeService attributeService;
    private JsonBuilderStrategy jsonBuilderStrategy;
    private CleanUpStrategy cleanUpStrategy;
    private NodeRefUtil nodeRefUtil;
    private Upload req;

    @Override
    public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) {
        try {
                req = RequestResponseHandler.deserialize(
                    webScriptRequest.getContent().getContent(),
                    Upload.class
            );

            Map<String, String> documentDetails = nodeRefUtil.getUploadDocumentDetails(req.getPreUploadId());

            // NOTE: this is NOT a multipart/form-data request. It is a normal POST request to a
            // separate service that in turn will perform the actual multipart/form-data request

            MultipartRequest multipartRequest = new MultipartRequest(
                    jsonBuilderStrategy.build(this),
                    documentDetails.get("filename"),
                    documentDetails.get("mimeType"),
                    req.getToken().get("token"),
                    documentDetails.get("contentStorePath")
            );

            String response = HttpHandler.POST_MULTIPART(multipartRequest);

            logger.debug("Document uploaded");

            nodeRefUtil.deleteNode(req.getPreUploadId());
            cleanUpStrategy.cleanUp(this);

            logger.debug("Document deleted");

            RequestResponseHandler.writeWebscriptResponse(webScriptResponse, "{\"msg\":\"success\"}");

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
