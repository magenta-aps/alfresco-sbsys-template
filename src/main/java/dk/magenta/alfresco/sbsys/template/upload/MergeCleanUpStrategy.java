package dk.magenta.alfresco.sbsys.template.upload;

import dk.magenta.alfresco.sbsys.template.Constants;
import dk.magenta.alfresco.sbsys.template.json.Upload;
import org.alfresco.service.cmr.attributes.AttributeService;

public class MergeCleanUpStrategy implements CleanUpStrategy {
    @Override
    public void cleanUp(UploadDocument uploadDocument) {
        AttributeService attributeService = uploadDocument.getAttributeService();
        Upload req = uploadDocument.getReq();

        attributeService.removeAttribute(req.getPreUploadId(), Constants.CASE_ID);
        attributeService.removeAttribute(req.getPreUploadId(), Constants.DOCUMENT_NAME);
        attributeService.removeAttribute(req.getPreUploadId(), Constants.URL);
    }
}
