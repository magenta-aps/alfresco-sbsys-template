package dk.magenta.alfresco.sbsys.template;

import com.google.gson.Gson;
import dk.magenta.alfresco.sbsys.template.json.DocumentReceiver;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.site.SiteService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class UploadDocument extends AbstractWebScript {

    private static Log logger = LogFactory.getLog(UploadDocument.class);

    private ContentService contentService;
    private FileFolderService fileFolderService;
    private Properties properties;
    private SiteService siteService;

    @Override
    public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) {
        try {
            DocumentReceiver req = RequestResponseHandler.deserialize(
                    webScriptRequest.getContent().getContent(),
                    DocumentReceiver.class
            );

            String json = "{\"SagID\":979,\"Navn\":\"NavnABC\",\"Beskrivelse\":\"Dette er en beskrivelse\"}";



            logger.debug(req.getPreUploadId());

        } catch (IOException e) {
            e.printStackTrace();
            throw new AlfrescoRuntimeException(e.getMessage());
        }
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
}
