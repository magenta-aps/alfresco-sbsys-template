package dk.magenta.alfresco.sbsys.template.editpreview;

import dk.magenta.alfresco.sbsys.template.Constants;
import org.alfresco.service.cmr.repository.NodeRef;

import java.util.Properties;

public class OnlyOfficeUrlStrategy implements UrlStrategy {
    @Override
    public String getUrl(String operation, NodeRef nodeRef, Properties properties) {
        // TODO: do not handle variability parametric

        String commonUrl = properties.getProperty("alfresco.protocol") +
                "://" +
                properties.getProperty("alfresco.host") +
                "/share/page";

        if (operation.equals(Constants.PREVIEW)) {
            return commonUrl + "/iframe-preview?nodeRef=" + nodeRef.toString();
        } else if (operation.equals(Constants.EDIT)) {
            return commonUrl +
                    "/site/" +
                    properties.getProperty("sbsys.template.site") +
                    "/onlyoffice-edit?nodeRef=" + nodeRef.toString() +
                    "&new=";
        } else {
            // Should never happen
            return null;
        }
    }
}
