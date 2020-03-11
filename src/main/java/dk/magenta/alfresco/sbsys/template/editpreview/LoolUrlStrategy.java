package dk.magenta.alfresco.sbsys.template.editpreview;

import org.alfresco.service.cmr.repository.NodeRef;

import java.util.Properties;

public class LoolUrlStrategy implements UrlStrategy {

    @Override
    public String getUrl(String operation, NodeRef nodeRef, Properties properties) {
        return properties.getProperty("alfresco.protocol") + "://" + properties.getProperty("alfresco.host") +
                "/opendesk/edit/" +
                properties.getProperty("sbsys.template.site") +
                "/" +
                nodeRef.getId();
    }
}
