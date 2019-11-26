package dk.magenta.alfresco.sbsys.template;

import org.alfresco.model.ContentModel;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteService;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class NodeRefUtil {

    private ContentService contentService;
    private Properties properties;
    private SiteService siteService;

    private static final String CONTAINER = "documentLibrary";
    private static final String MIMETYPE_WORD = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private static final String SITE_PROPERTY = "sbsys.template.site";

    public NodeRef getDocLib() {

        // TODO: handle case where site does not exists

        PagingRequest pagingRequest = new PagingRequest(Integer.MAX_VALUE);
        PagingResults<FileInfo> containers = siteService.listContainers(getSite(), pagingRequest);
        return siteService.getContainer(getSite(), CONTAINER);
    }

    public InputStream getInputStream(String nodeRef) {
        ContentReader contentReader = contentService.getReader(
                new NodeRef(nodeRef),
                ContentModel.PROP_CONTENT
        );
        return contentReader.getContentInputStream();
    }

    public OutputStream getOutputStream(NodeRef nodeRef) {
        ContentWriter contentWriter = contentService.getWriter(
                nodeRef,
                ContentModel.PROP_CONTENT,
                true
        );
        contentWriter.setMimetype(MIMETYPE_WORD);
        return contentWriter.getContentOutputStream();
    }

     /**
     * Get site short name from alfresco-global.properties
     * @return Site short name
     */
    private String getSite() {
        return properties.getProperty(SITE_PROPERTY);
    }

    public void setContentService(ContentService contentService) { this.contentService = contentService; }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
}
