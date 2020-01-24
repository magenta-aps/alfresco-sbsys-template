package dk.magenta.alfresco.sbsys.template;

import com.google.gson.JsonSyntaxException;
import dk.magenta.alfresco.sbsys.template.json.PreviewRequest;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Preview extends AbstractWebScript {
    private static Log logger = LogFactory.getLog(Preview.class);

    private ContentService contentService;
    private FileFolderService fileFolderService;
    private NodeRefUtil nodeRefUtil;
    private Properties properties;

    public static final String PREVIEW_FOLDER = "preview";

    @Override
    public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) throws IOException {
        logger.debug("Preview webscript called");
        try {

            // Get POSTed JSON as string from request and deserialize into POJO
            PreviewRequest req = RequestResponseHandler.deserialize(
                    webScriptRequest.getContent().getContent(),
                    PreviewRequest.class
            );

            // Get content InputStream from SBSYS
            InputStream content = HttpHandler.GET_CONTENT(
                    String.format(properties.getProperty("sbsys.template.url.get.draft"), req.getKladdeID()),
                    req.getToken().get(MergeData.TOKEN)
            );

            // TODO: refactor common code with MergeData webscript

            // Get the preview folder
            List<FileInfo> docLibFolders = fileFolderService.listFolders(nodeRefUtil.getDocLib());
            FileInfo preview = docLibFolders.stream()
                    .filter((FileInfo fileInfo) -> fileInfo.getName().equals(PREVIEW_FOLDER))
                    .findFirst()
                    .get();

            // Create document in Alfresco

            FileInfo previewDoc = fileFolderService.create(
                    preview.getNodeRef(),
                    req.getFilnavn(),
                    ContentModel.TYPE_CONTENT
            );

            ContentWriter writer = contentService.getWriter(
                    preview.getNodeRef(),
                    ContentModel.PROP_CONTENT,
                    true
            );
            writer.guessMimetype(req.getFilnavn());
            writer.putContent(content);

            content.close();

            // Make response

            Map<String, String> resp = new HashMap<>();
            resp.put("msg", "success");

            String json = RequestResponseHandler.serialize(resp);
            logger.debug(json);
            RequestResponseHandler.writeWebscriptResponse(webScriptResponse, json);

        } catch (JsonSyntaxException e) {
            webScriptResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
            RequestResponseHandler.writeWebscriptResponse(
                    webScriptResponse,
                    RequestResponseHandler.getJsonSyntaxErrorMessage()
            );
        }
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
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
