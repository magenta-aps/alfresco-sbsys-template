package dk.magenta.alfresco.sbsys.template.editpreview;

import dk.magenta.alfresco.sbsys.template.Constants;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.NodeRef;

import java.util.Properties;

public class CommonUrlStrategy implements UrlStrategy {

    private UrlStrategy previewUrlStrategy;
    private UrlStrategy onlyOfficeUrlStrategy;

    @Override
    public String getUrl(String operation, NodeRef nodeRef, Properties properties) {
        if (operation.equals(Constants.PREVIEW)) {
            return previewUrlStrategy.getUrl(operation, nodeRef, properties);
        } else if (operation.equals(Constants.EDIT)) {
            return onlyOfficeUrlStrategy.getUrl(operation, nodeRef, properties);
        } else {
            throw new AlfrescoRuntimeException("Not strategy for generating URL");
        }
    }

    public void setPreviewUrlStrategy(UrlStrategy previewUrlStrategy) {
        this.previewUrlStrategy = previewUrlStrategy;
    }

    public void setOnlyOfficeUrlStrategy(UrlStrategy onlyOfficeUrlStrategy) {
        this.onlyOfficeUrlStrategy = onlyOfficeUrlStrategy;
    }
}
