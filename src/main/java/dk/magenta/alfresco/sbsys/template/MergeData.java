package dk.magenta.alfresco.sbsys.template;

import dk.magenta.alfresco.sbsys.template.json.Case;
import dk.magenta.alfresco.sbsys.template.json.TemplateReceiver;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.util.GUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.wickedsource.docxstamper.DocxStamper;
import org.wickedsource.docxstamper.DocxStamperConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MergeData extends AbstractWebScript {
    private static Log logger = LogFactory.getLog(MergeData.class);

    private AttributeService attributeService;
    private FileFolderService fileFolderService;
    private NodeRefUtil nodeRefUtil;

    private static final String TOKEN = "token";
    private static final String PREUPLOAD_FOLDER = "pre-upload";

    @Override
    public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) {
        try {

            ////////////////////////// Get request body ///////////////////////////

            // Get POSTed JSON as string from request and deserialize into POJO
            TemplateReceiver req = RequestResponseHandler.deserialize(
                    webScriptRequest.getContent().getContent(),
                    TemplateReceiver.class
            );

            // Call SBSYS to get case metadata
            // TODO: fix hardcoded URL
            String response = HttpHandler.GET(
                    "https://sbsip-m-01.bk-sbsys.dk:28443/convergens-sbsip-sbsys-webapi-proxy/proxy/api/sag/" + req.kladde.get("SagID"),
                    req.token.get(TOKEN)
            );
            logger.debug(response);
            Case sbsysCase = RequestResponseHandler.deserialize(response, Case.class);

            ///////////////////// Merge data into template ////////////////////////

            // Get InputStream for template document
            // TODO: The NodeRef should be constructed in a better way
            InputStream inputStream = nodeRefUtil.getInputStream("workspace://SpacesStore/" + req.id);

            // Get the pre-upload folder
            List<FileInfo> docLibFolders = fileFolderService.listFolders(nodeRefUtil.getDocLib());
            FileInfo preUpload = docLibFolders.stream()
                    .filter((FileInfo fileInfo) -> fileInfo.getName().equals(PREUPLOAD_FOLDER))
                    .findFirst()
                    .get();

            // Create the merged document
            String preUploadFilename = GUID.generate();
            FileInfo mergedDoc = fileFolderService.create(
                    preUpload.getNodeRef(),
                    preUploadFilename + ".docx",
                    ContentModel.TYPE_CONTENT
            );

            OutputStream outputStream = nodeRefUtil.getOutputStream(mergedDoc.getNodeRef());

            // Merge data into template
            DocxStamper stamper = new DocxStamperConfiguration().build();
            stamper.stamp(inputStream, sbsysCase, outputStream);

            inputStream.close();
            outputStream.close();

            ///////////// Store caseId in the AttributeService /////////////

            attributeService.createAttribute(req.kladde.get("SagID"), mergedDoc.getNodeRef().toString());

            /////////////////////// Build response /////////////////////////

            Map<String, String> resp = new HashMap<>();
            // TODO: remove magic keys/values
            resp.put("preUploadId", mergedDoc.getNodeRef().toString());
            resp.put("preUploadFilename", preUploadFilename);
            resp.put("url", "https://alfrescoskabelon.magenta.dk/share/page/site/swsdp/onlyoffice-edit?nodeRef=" + mergedDoc.getNodeRef().toString());

            String json = RequestResponseHandler.serialize(resp);
            RequestResponseHandler.writeWebscriptResponse(webScriptResponse, json);

        } catch (IOException e) {
            e.printStackTrace();
            throw new AlfrescoRuntimeException(e.getMessage());
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

    public void setNodeRefUtil(NodeRefUtil nodeRefUtil) {
        this.nodeRefUtil = nodeRefUtil;
    }
}
