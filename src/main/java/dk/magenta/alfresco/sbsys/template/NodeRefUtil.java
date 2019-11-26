package dk.magenta.alfresco.sbsys.template;

import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteService;

import java.util.Properties;

public class NodeRefUtil {

    private Properties properties;
    private SiteService siteService;

    private static final String CONTAINER = "documentLibrary";
    private static final String SITE_PROPERTY = "sbsys.template.site";

    public NodeRef getDocLib() {
        PagingRequest pagingRequest = new PagingRequest(Integer.MAX_VALUE);
        PagingResults<FileInfo> containers = siteService.listContainers(getSite(), pagingRequest);
        return siteService.getContainer(getSite(), CONTAINER);
    }

     /**
     * Get site short name from alfresco-global.properties
     * @return Site short name
     */
    private String getSite() {
        return properties.getProperty(SITE_PROPERTY);
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
}
