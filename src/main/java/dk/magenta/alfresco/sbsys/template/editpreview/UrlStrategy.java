package dk.magenta.alfresco.sbsys.template.editpreview;

import org.alfresco.service.cmr.repository.NodeRef;

import java.util.Properties;

public interface UrlStrategy {
    String getUrl(String operation, NodeRef nodeRef, Properties properties);
}
