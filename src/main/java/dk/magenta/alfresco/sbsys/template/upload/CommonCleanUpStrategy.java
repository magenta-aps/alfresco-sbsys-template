package dk.magenta.alfresco.sbsys.template.upload;

import dk.magenta.alfresco.sbsys.template.utils.Constants;
import dk.magenta.alfresco.sbsys.template.json.Upload;
import org.alfresco.service.cmr.attributes.AttributeService;

public class CommonCleanUpStrategy implements CleanUpStrategy {

    private CleanUpStrategy mergeCleanUpStrategy;
    private CleanUpStrategy checkoutCleanUpStrategy;

    @Override
    public void cleanUp(UploadDocument uploadDocument) {

        // State design pattern

        AttributeService attributeService = uploadDocument.getAttributeService();
        Upload req = uploadDocument.getReq();
        String operation = (String) attributeService.getAttribute(req.getPreUploadId(), Constants.OPERATION);

        if (operation.equals(Constants.MERGE)) {
            mergeCleanUpStrategy.cleanUp(uploadDocument);
        } else if (operation.equals(Constants.CHECKOUT)) {
            checkoutCleanUpStrategy.cleanUp(uploadDocument);
        } else {
            // should never happen
        }
    }

    public void setMergeCleanUpStrategy(CleanUpStrategy mergeCleanUpStrategy) {
        this.mergeCleanUpStrategy = mergeCleanUpStrategy;
    }

    public void setCheckoutCleanUpStrategy(CleanUpStrategy checkoutCleanUpStrategy) {
        this.checkoutCleanUpStrategy = checkoutCleanUpStrategy;
    }
}
