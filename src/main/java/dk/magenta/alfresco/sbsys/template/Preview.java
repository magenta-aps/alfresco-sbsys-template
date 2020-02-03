package dk.magenta.alfresco.sbsys.template;

import com.google.gson.JsonSyntaxException;
import dk.magenta.alfresco.sbsys.template.json.PreviewRequest;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.GUID;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.ByteArrayInputStream;
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
    private MimetypeService mimetypeService;
    private NodeRefUtil nodeRefUtil;
    private Properties properties;

    private static final String FILDOWNLOAD = "fildownload";
    public static final String PREVIEW_FOLDER = "preview";
    private static final int STREAM_MARK_BUFFER = 1000;

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
            byte[] content = HttpHandler.GET_CONTENT(
                    req.getUrls().get(FILDOWNLOAD),
                    req.getToken().get(MergeData.TOKEN)
            );
            InputStream in = new ByteArrayInputStream(content);

            // Guess the content mimetype
            in.mark(STREAM_MARK_BUFFER);
            String mimetypeGuess = mimetypeService.guessMimetype("unknown", in);
            in.reset();

            // TODO: refactor common code with MergeData webscript

            // Get the preview folder
            List<FileInfo> docLibFolders = fileFolderService.listFolders(nodeRefUtil.getDocLib());
            FileInfo preview = docLibFolders.stream()
                    .filter((FileInfo fileInfo) -> fileInfo.getName().equals(PREVIEW_FOLDER))
                    .findFirst()
                    .get();

            // Create document in Alfresco

            String previewFilename = GUID.generate();
            FileInfo previewDoc = fileFolderService.create(
                    preview.getNodeRef(),
                    previewFilename,
                    ContentModel.TYPE_CONTENT
            );

            ContentWriter writer = contentService.getWriter(
                    previewDoc.getNodeRef(),
                    ContentModel.PROP_CONTENT,
                    true
            );
            writer.setMimetype(mimetypeGuess);
            writer.putContent(in);

            // in.close(); // Not necessary for ByteArrayInputStream

            // Make response

            Map<String, String> resp = new HashMap<>();
            resp.put("url", getPreviewUrl(previewDoc.getNodeRef()));

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

    private String getPreviewUrl(NodeRef nodeRef) {
        return properties.getProperty("alfresco.protocol") +
                "://" +
                properties.getProperty("alfresco.host") +
                "/share/page/iframe-preview?nodeRef=" +
                nodeRef.toString();
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    public void setMimetypeService(MimetypeService mimetypeService) {
        this.mimetypeService = mimetypeService;
    }

    public void setNodeRefUtil(NodeRefUtil nodeRefUtil) {
        this.nodeRefUtil = nodeRefUtil;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
