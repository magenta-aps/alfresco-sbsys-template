package dk.magenta.alfresco.sbsys.template.edit;

import com.google.gson.JsonSyntaxException;
import dk.magenta.alfresco.sbsys.template.Constants;
import dk.magenta.alfresco.sbsys.template.HttpHandler;
import dk.magenta.alfresco.sbsys.template.NodeRefUtil;
import dk.magenta.alfresco.sbsys.template.RequestResponseHandler;
import dk.magenta.alfresco.sbsys.template.json.UrlsAndTokenRequest;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.MimetypeService;
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
import java.util.List;
import java.util.Map;

public class PreviewAndEdit extends AbstractWebScript {
    private static Log logger = LogFactory.getLog(PreviewAndEdit.class);

    private AttributeService attributeService;
    private ContentService contentService;
    private FileFolderService fileFolderService;
    private FileLocationProvider fileLocationProvider;
    private MimetypeService mimetypeService;
    private NodeRefUtil nodeRefUtil;

    private static final String FILDOWNLOAD = "fildownload";
    private static final int STREAM_MARK_BUFFER = 1000;

    @Override
    public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) throws IOException {
        logger.debug("Preview webscript called");

        // TODO: throw exception for unknown operation
        String operation = webScriptRequest.getServiceMatch().getTemplateVars().get(Constants.OPERATION);

        try {

            // Get POSTed JSON as string from request and deserialize into POJO
            UrlsAndTokenRequest req = RequestResponseHandler.deserialize(
                    webScriptRequest.getContent().getContent(),
                    UrlsAndTokenRequest.class
            );

            String urlKey = operation.equals(Constants.EDIT) ? Constants.FILCHECKUD : FILDOWNLOAD;
            logger.debug("urlKey: " + urlKey);

            // Get content InputStream from SBSYS
            byte[] content = HttpHandler.GET_CONTENT(
                    req.getUrls().getOrDefault(Constants.FILCHECKUD, req.getUrls().get(FILDOWNLOAD)),
                    req.getToken().get(Constants.TOKEN)
            );
            InputStream in = new ByteArrayInputStream(content);
            logger.debug("Download URL: " + req.getUrls().get(urlKey));

            // Guess the content mimetype
            in.mark(STREAM_MARK_BUFFER);
            String mimetypeGuess = mimetypeService.guessMimetype("unknown", in);
            in.reset();
            logger.debug("Mimetype guess: " + mimetypeGuess);

            // TODO: refactor common code with MergeData webscript

            // Get the preview folder
            List<FileInfo> docLibFolders = fileFolderService.listFolders(nodeRefUtil.getDocLib());
            FileInfo preview = docLibFolders.stream()
                    .filter((FileInfo fileInfo) -> fileInfo.getName().equals(operation))
                    .findFirst()
                    .get();

            // Create document in Alfresco
            String previewFilename = GUID.generate() + "." + mimetypeService.getExtension(mimetypeGuess);
            FileInfo previewDoc = fileFolderService.create(
                    preview.getNodeRef(),
                    previewFilename,
                    ContentModel.TYPE_CONTENT
            );

            // Add versionable aspect
            nodeRefUtil.setVersionableAspect(previewDoc.getNodeRef());

            ContentWriter writer = contentService.getWriter(
                    previewDoc.getNodeRef(),
                    ContentModel.PROP_CONTENT,
                    true
            );
            writer.setMimetype(mimetypeGuess);
            writer.putContent(in);

            // in.close(); // Not necessary for ByteArrayInputStream

            if (operation.equals(Constants.EDIT)) {
                attributeService.createAttribute(Constants.CHECKOUT, previewDoc.getNodeRef().toString(), Constants.OPERATION);
                attributeService.createAttribute(req.getUrls().get(Constants.FILCHECKIND), previewDoc.getNodeRef().toString(), Constants.URL);
            }

            // Make response

            Map<String, String> resp = fileLocationProvider.getEditingFileLocationData(
                    previewDoc.getNodeRef(),
                    previewFilename,
                    operation
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
        }
    }

    /////////////////////// Setters ////////////////////////////

    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setFileLocationProvider(FileLocationProvider fileLocationProvider) {
        this.fileLocationProvider = fileLocationProvider;
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

}
