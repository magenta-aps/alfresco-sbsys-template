package dk.magenta.alfresco.sbsys.template;

import dk.magenta.alfresco.sbsys.template.json.DocumentReceiver;
import dk.magenta.alfresco.sbsys.template.json.MultipartRequest;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class UploadDocument extends AbstractWebScript {

    private static Log logger = LogFactory.getLog(UploadDocument.class);

    private AttributeService attributeService;
    private FileFolderService fileFolderService;
    private NodeRefUtil nodeRefUtil;
    private Properties properties;

    @Override
    public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) {
        try {
            DocumentReceiver req = RequestResponseHandler.deserialize(
                    webScriptRequest.getContent().getContent(),
                    DocumentReceiver.class
            );

            // NOTE: this is NOT a multipart/form-data request. It is a normal POST request to a
            // separate service that in turn will perform the actual multipart/form-data request

            // String sagId = (String) attributeService.getAttribute(req.preUploadId);
            String sagId = "979";
            Map<String, String> documentDetails = nodeRefUtil.getUploadDocumentDetails(req.getPreUploadId());

            MultipartRequest multipartRequest = new MultipartRequest(
                    Integer.parseInt(sagId),
                    "Kladde " + sagId,
                    documentDetails.get("filename"),
                    documentDetails.get("mimeType"),
                    req.getToken().get("token"),
                    documentDetails.get("contentStorePath")
                    );

            String response = HttpHandler.POST_MULTIPART(multipartRequest);

            logger.debug("Document uploaded");

        } catch (IOException e) {
            e.printStackTrace();
            throw new AlfrescoRuntimeException(e.getMessage());
        }
    }

    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
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
