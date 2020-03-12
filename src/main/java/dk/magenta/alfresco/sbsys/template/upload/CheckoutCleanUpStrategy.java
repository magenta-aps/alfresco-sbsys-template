package dk.magenta.alfresco.sbsys.template.upload;

import dk.magenta.alfresco.sbsys.template.utils.Constants;
import dk.magenta.alfresco.sbsys.template.json.Upload;
import org.alfresco.service.cmr.attributes.AttributeService;

public class CheckoutCleanUpStrategy implements CleanUpStrategy {
    @Override
    public void cleanUp(UploadDocument uploadDocument) {
        AttributeService attributeService = uploadDocument.getAttributeService();
        Upload req = uploadDocument.getReq();

        attributeService.removeAttribute(req.getPreUploadId(), Constants.URL);
    }
}
