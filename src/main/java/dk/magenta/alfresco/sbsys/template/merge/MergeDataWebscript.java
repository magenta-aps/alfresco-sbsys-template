package dk.magenta.alfresco.sbsys.template.merge;

import com.google.gson.JsonSyntaxException;
import dk.magenta.alfresco.sbsys.template.*;
import dk.magenta.alfresco.sbsys.template.json.Case;
import dk.magenta.alfresco.sbsys.template.json.Merge;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.util.GUID;
import org.alfresco.util.Pair;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.wickedsource.docxstamper.DocxStamper;
import org.wickedsource.docxstamper.DocxStamperConfiguration;
import org.wickedsource.docxstamper.api.DocxStamperException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MergeDataWebscript extends AbstractWebScript {
    private static Log logger = LogFactory.getLog(MergeDataWebscript.class);

    private AttributeService attributeService;
    private FileFolderService fileFolderService;
    private MergeStrategy mergeStrategy;
    private NodeRefUtil nodeRefUtil;
    private PreviewAndEdit previewAndEdit;
    private Properties properties;

    private InputStream inputStream;
    private OutputStream outputStream;
    private Case sbsysCase;
    private Pair<String, String> mimetypeExtension;

    private static final String OPRET_KLADDE = "opretkladde";

    @Override
    public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) {
        logger.debug("Merge webscript called");

        try {

            ////////////////////////// Get request body ///////////////////////////

            // Get POSTed JSON as string from request and deserialize into POJO
            Merge req = RequestResponseHandler.deserialize(
                    webScriptRequest.getContent().getContent(),
                    Merge.class
            );

            // Call SBSYS to get case metadata
            String response = HttpHandler.GET_JSON(
                    properties.getProperty("sbsys.template.url.get.case") + "/" + req.getKladde().getSagID(),
                    req.getToken().get(Constants.TOKEN)
            );
            logger.debug(response);
            sbsysCase = RequestResponseHandler.deserialize(response, Case.class);

            ///////////////////// Merge data into template ////////////////////////

            // Get mimetype and filename extension for the template
            mimetypeExtension = nodeRefUtil.getFileType(req.getId());

            // Get InputStream for template document
            inputStream = nodeRefUtil.getInputStream(Constants.WORKSPACE_SPACESSTORE + req.getId());

            // Get the pre-upload folder
            List<FileInfo> docLibFolders = fileFolderService.listFolders(nodeRefUtil.getDocLib());
            FileInfo preUpload = docLibFolders.stream()
                    .filter((FileInfo fileInfo) -> fileInfo.getName().equals(Constants.PREUPLOAD_FOLDER))
                    .findFirst()
                    .get();

            // Create the merged document
            String preUploadFilename = GUID.generate();
            FileInfo mergedDoc = fileFolderService.create(
                    preUpload.getNodeRef(),
                    preUploadFilename + mimetypeExtension.getSecond(),
                    ContentModel.TYPE_CONTENT
            );

            // Add versionable aspect
            nodeRefUtil.setVersionableAspect(mergedDoc.getNodeRef());

            // Set the date
            sbsysCase.setDato(nodeRefUtil.getNodeRefCreationDate(mergedDoc));

            // Get outputStream for merged document
            outputStream = nodeRefUtil.getOutputStream(mergedDoc.getNodeRef(), mimetypeExtension.getFirst());

            // Merge data into template
            mergeStrategy.merge(this);

            ///////////// Store caseId and document name in the AttributeService /////////////

            attributeService.createAttribute(Constants.MERGE, mergedDoc.getNodeRef().toString(), Constants.OPERATION);
            attributeService.createAttribute(req.getKladde().getSagID(), mergedDoc.getNodeRef().toString(), Constants.CASE_ID);
            attributeService.createAttribute(req.getKladde().getNavn(), mergedDoc.getNodeRef().toString(), Constants.DOCUMENT_NAME);
            attributeService.createAttribute(req.getUrls().get(OPRET_KLADDE), mergedDoc.getNodeRef().toString(), Constants.URL);

            /////////////////////// Build response /////////////////////////

            Map<String, String> resp = previewAndEdit.getEditingFileLocationData(
                    mergedDoc.getNodeRef(),
                    preUploadFilename + mimetypeExtension.getSecond(),
                    Constants.EDIT);

            String json = RequestResponseHandler.serialize(resp);
            logger.debug(json);
            RequestResponseHandler.writeWebscriptResponse(webScriptResponse, json);

        } catch (JsonSyntaxException e) {
            webScriptResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
            RequestResponseHandler.writeWebscriptResponse(
                    webScriptResponse,
                    RequestResponseHandler.getJsonSyntaxErrorMessage()
            );
        } catch (IOException e) {
            e.printStackTrace();
            throw new AlfrescoRuntimeException(e.getMessage());
        } catch (DocxStamperException e) {
            e.printStackTrace();
            webScriptResponse.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            RequestResponseHandler.writeWebscriptResponse(
                    webScriptResponse,
                    RequestResponseHandler.getMergeErrorMessage()
            );
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    ////////////////////// Getters and setters /////////////////////////

    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    public FileFolderService getFileFolderService() {
        return fileFolderService;
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    public void setMergeStrategy(MergeStrategy mergeStrategy) {
        this.mergeStrategy = mergeStrategy;
    }

    public void setNodeRefUtil(NodeRefUtil nodeRefUtil) {
        this.nodeRefUtil = nodeRefUtil;
    }

    public void setPreviewAndEdit(PreviewAndEdit previewAndEdit) {
        this.previewAndEdit = previewAndEdit;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    InputStream getInputStream() {
        return inputStream;
    }

    OutputStream getOutputStream() {
        return outputStream;
    }

    Case getSbsysCase() {
        return sbsysCase;
    }

    Pair<String, String> getMimetypeExtension() {
        return mimetypeExtension;
    }
}
