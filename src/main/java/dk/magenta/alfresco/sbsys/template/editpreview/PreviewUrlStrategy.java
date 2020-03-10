package dk.magenta.alfresco.sbsys.template.editpreview;

import org.alfresco.service.cmr.repository.NodeRef;

import java.util.Properties;

public class PreviewUrlStrategy implements UrlStrategy {
    @Override
    public String getUrl(String operation, NodeRef nodeRef, Properties properties) {
        return  properties.getProperty("alfresco.protocol") + "://" +
                properties.getProperty("alfresco.host") + "/share/page/iframe-preview?nodeRef=" + nodeRef.toString();
    }
}
