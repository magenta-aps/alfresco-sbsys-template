package dk.magenta.alfresco.sbsys.template.editpreview;

import dk.magenta.alfresco.sbsys.template.Constants;
import dk.magenta.alfresco.sbsys.template.NodeRefUtil;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.Pair;

import java.util.Properties;

public class CommonUrlStrategy implements UrlStrategy {

    private NodeRefUtil nodeRefUtil;

    private UrlStrategy loolUrlStrategy;
    private UrlStrategy onlyOfficeUrlStrategy;
    private UrlStrategy previewUrlStrategy;

    @Override
    public String getUrl(String operation, NodeRef nodeRef, Properties properties) {
        if (operation.equals(Constants.PREVIEW)) {
            return previewUrlStrategy.getUrl(operation, nodeRef, properties);
        } else if (operation.equals(Constants.EDIT)) {
            Pair<String, String> mimetypeExtension = nodeRefUtil.getFileType(nodeRef.getId());
            String extension = mimetypeExtension.getSecond();
            if (extension.equals(Constants.DOCX)) {
                return onlyOfficeUrlStrategy.getUrl(operation, nodeRef, properties);
            } else if (extension.equals(Constants.ODT)) {
                return loolUrlStrategy.getUrl(operation, nodeRef, properties);
            } else {
                throw new AlfrescoRuntimeException("URL generation error: unknown file extension");
            }
        } else {
            throw new AlfrescoRuntimeException("URL generation error: unknown operation");
        }
    }

    public void setNodeRefUtil(NodeRefUtil nodeRefUtil) {
        this.nodeRefUtil = nodeRefUtil;
    }

    public void setLoolUrlStrategy(UrlStrategy loolUrlStrategy) {
        this.loolUrlStrategy = loolUrlStrategy;
    }

    public void setOnlyOfficeUrlStrategy(UrlStrategy onlyOfficeUrlStrategy) {
        this.onlyOfficeUrlStrategy = onlyOfficeUrlStrategy;
    }

    public void setPreviewUrlStrategy(UrlStrategy previewUrlStrategy) {
        this.previewUrlStrategy = previewUrlStrategy;
    }

}
