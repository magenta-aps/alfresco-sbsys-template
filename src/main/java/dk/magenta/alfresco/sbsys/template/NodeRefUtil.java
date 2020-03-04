package dk.magenta.alfresco.sbsys.template;

import dk.magenta.alfresco.sbsys.template.exceptions.VersionUploadException;
import org.alfresco.model.ContentModel;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.domain.node.ContentDataWithId;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;

public class NodeRefUtil {

    private static Log logger = LogFactory.getLog(NodeRefUtil.class);

    private ContentService contentService;
    private NodeService nodeService;
    private Properties properties;
    private SiteService siteService;

    private static final String CONTAINER = "documentLibrary";
    private static final String MIMETYPE_WORD = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private static final String SITE_PROPERTY = "sbsys.template.site";
    private static final String CONTENT_STORE_PATH = "sbsys.template.contentstore";

    public void deleteNode(String nodeRef) {
        nodeService.deleteNode(new NodeRef(nodeRef));
    }

    public Date getNodeRefCreationDate(FileInfo fileInfo) {
        return (Date) nodeService.getProperty(
                fileInfo.getNodeRef(),
                ContentModel.PROP_CREATED
        );
    }

    public NodeRef getDocLib() {

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

    public Pair<String, String> getFileType(String nodeRefUuid) {
        NodeRef nodeRef = new NodeRef(Constants.WORKSPACE_SPACESSTORE + nodeRefUuid);
        ContentDataWithId contentData = (ContentDataWithId) nodeService.getProperty(nodeRef, ContentModel.PROP_CONTENT);
        String name = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
        return new Pair<>(contentData.getMimetype(), "." + name.split("\\.")[1]);
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
     * Get info about the document to upload (filename, mimetype,...)
     */
    public Map<String, String> getUploadDocumentDetails(String node) {
        NodeRef nodeRef = new NodeRef(node);

        String filename = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
        ContentDataWithId contentData = (ContentDataWithId) nodeService.getProperty(nodeRef, ContentModel.PROP_CONTENT);
        String contentUrl = contentData.getContentUrl();
        String mimeType = contentData.getMimetype();

        String contentStorePath = properties.get(CONTENT_STORE_PATH) + contentUrl.substring(7);

        Map<String, String> documentDetails = new HashMap<>();
        documentDetails.put("filename", filename);
        documentDetails.put("mimeType", mimeType);
        documentDetails.put("contentStorePath", contentStorePath);

        return documentDetails;
    }

    public void verifyVersionForUpload(String nodeRefStr) throws VersionUploadException {
        NodeRef nodeRef = new NodeRef(nodeRefStr);
        String version = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_VERSION_LABEL);
        logger.debug("Version: " + version);

        if (version.equals("1.0")) {
            throw new VersionUploadException("Not allowed to upload version 1.0 to SBSYS");
        }
    }

    public void setVersionableAspect(NodeRef nodeRef) {
        Map<QName, Serializable> properties = new HashMap<>();
        properties.put(ContentModel.PROP_VERSION_LABEL, "1.0");
        nodeService.addAspect(nodeRef, ContentModel.ASPECT_VERSIONABLE, properties);
    }

    public boolean shouldDocumentBeUploaded(NodeRef nodeRef, Version version) {
        logger.debug("Version = " + version.getVersionLabel());

        if (!version.getVersionLabel().equals("1.1")) {
            return false;
        }

        List<ChildAssociationRef> parentAssocs = nodeService.getParentAssocs(nodeRef);
        if (parentAssocs.size() != 1) {
            return false;
        }

        ChildAssociationRef parentAssoc = parentAssocs.get(0);
        NodeRef parent = parentAssoc.getParentRef();
        String name = (String) nodeService.getProperty(parent, ContentModel.PROP_NAME);
        if (name.equals(Constants.PREUPLOAD_FOLDER)) {
            return true;
        }

        return false;
    }

    /**
     * Get site short name from alfresco-global.properties
     *
     * @return Site short name
     */
    private String getSite() {
        return properties.getProperty(SITE_PROPERTY);
    }

    //////////////// Getters and setters /////////////////

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
}
