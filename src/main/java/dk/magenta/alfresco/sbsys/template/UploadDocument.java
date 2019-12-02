package dk.magenta.alfresco.sbsys.template;

import com.google.gson.Gson;
import dk.magenta.alfresco.sbsys.template.json.DocumentReceiver;
import dk.magenta.alfresco.sbsys.template.json.MultipartRequest;
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

            // NOTE: this is NOT a multipart/form-data request. It is a normal POST request to a
            // separate service that in turn will perform the actual multipart/form-data request

            MultipartRequest multipartRequest = new MultipartRequest(
                    979,
                    "TestNavn",
                    "test.docx",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    req.token.get("token"),
                    "/home/andreas/skabelon/alfresco-sbsys-template/alf_data_dev/contentstore/2019/11/26/14/3/ed5379f7-b504-482e-9fbb-e6cbcd182519.bin"
                    );

            String response = HttpHandler.POST_MULTIPART(multipartRequest);

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
