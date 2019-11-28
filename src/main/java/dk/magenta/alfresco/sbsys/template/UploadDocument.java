package dk.magenta.alfresco.sbsys.template;

import com.google.gson.Gson;
import dk.magenta.alfresco.sbsys.template.json.DocumentReceiver;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class UploadDocument extends AbstractWebScript {

    private static Log logger = LogFactory.getLog(UploadDocument.class);

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

            String json = "{\"SagID\":979,\"Navn\":\"NavnABC\",\"Emne\":\"Emne\",\"Beskrivelse\":\"Dette er en beskrivelse\"}";
            InputStream inputStream = nodeRefUtil.getInputStream(req.preUploadId);

            // TODO: handle magic values
            String response = HttpHandler.POST_MULTIPART(
                    "https://sbsip-m-01.bk-sbsys.dk:28443/convergens-sbsip-sbsys-webapi-proxy/proxy/api/kladde",
                    req.token.get("token"),
                    json,
                    inputStream
            );

            inputStream.close();

            logger.debug(req.preUploadId);

        } catch (IOException e) {
            e.printStackTrace();
            throw new AlfrescoRuntimeException(e.getMessage());
        }
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
