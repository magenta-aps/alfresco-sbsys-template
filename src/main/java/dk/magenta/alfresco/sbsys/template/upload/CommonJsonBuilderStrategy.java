package dk.magenta.alfresco.sbsys.template.upload;

import dk.magenta.alfresco.sbsys.template.Constants;
import dk.magenta.alfresco.sbsys.template.json.Upload;
import org.alfresco.service.cmr.attributes.AttributeService;

public class CommonJsonBuilderStrategy implements JsonBuilderStrategy {

    private JsonBuilderStrategy mergeJsonBuilderStrategy;
    private JsonBuilderStrategy checkoutJsonBuilderStrategy;

    @Override
    public String build(UploadDocument uploadDocument) {

        // State design pattern

        AttributeService attributeService = uploadDocument.getAttributeService();
        Upload req = uploadDocument.getReq();
        String operation = (String) attributeService.getAttribute(req.getPreUploadId(), Constants.OPERATION);

        if (operation.equals(Constants.MERGE)) {
            return mergeJsonBuilderStrategy.build(uploadDocument);
        } else if (operation.equals(Constants.CHECKOUT)) {
            return checkoutJsonBuilderStrategy.build(uploadDocument);
        } else {
            // Should never happen
        }
        return null;
    }

    public void setMergeJsonBuilderStrategy(JsonBuilderStrategy mergeJsonBuilderStrategy) {
        this.mergeJsonBuilderStrategy = mergeJsonBuilderStrategy;
    }

    public void setCheckoutJsonBuilderStrategy(JsonBuilderStrategy checkoutJsonBuilderStrategy) {
        this.checkoutJsonBuilderStrategy = checkoutJsonBuilderStrategy;
    }
}
