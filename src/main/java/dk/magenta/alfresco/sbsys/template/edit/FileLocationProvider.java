package dk.magenta.alfresco.sbsys.template.edit;

import dk.magenta.alfresco.sbsys.template.utils.Constants;
import dk.magenta.alfresco.sbsys.template.utils.NodeRefUtil;
import dk.magenta.libreoffice.online.service.LOOLService;
import dk.magenta.libreoffice.online.service.WOPIAccessTokenInfo;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class FileLocationProvider {

    private static Log logger = LogFactory.getLog(FileLocationProvider.class);

    private NodeRefUtil nodeRefUtil;
    private Properties properties;

    private LOOLService loolService;

    public Map<String, String> getEditingFileLocationData(NodeRef nodeRef, String filename, String operation) {
        Map<String, String> map = new HashMap<>();
        map.put("preUploadId", nodeRef.toString());
        map.put("preUploadFilename", filename);
        map.put("url", getUrl(operation, nodeRef));
        return map;
    }

    private String getUrl(String operation, NodeRef nodeRef) {

        // TODO: refactor if this works for LOOL...

        String url;
        String commonUrl = String.format(
                "%s://%s",
                properties.getProperty("alfresco.protocol"),
                properties.getProperty("alfresco.host")
        );

        if (operation.equals(Constants.PREVIEW)) {

            url = String.format(
                    "%s/share/page/iframe-preview?nodeRef=%s",
                    commonUrl,
                    nodeRef.toString()
            );

        } else if (operation.equals(Constants.EDIT)) {
            Pair<String, String> mimetypeExtension = nodeRefUtil.getFileType(nodeRef.getId());
            String extension = nodeRefUtil.getFileType(nodeRef.getId()).getSecond();
            if (extension.equals(Constants.DOCX) || extension.equals(Constants.XLSX)) {
                url = String.format(
                        "%s/share/page/site/%s/onlyoffice-edit?nodeRef=%s&new=",
                        commonUrl,
                        properties.getProperty("sbsys.template.site"),
                        nodeRef.toString()
                );
            } else if (extension.equals(Constants.ODT) || extension.equals(Constants.ODS)) {
                WOPIAccessTokenInfo tokenInfo = loolService.createAccessToken(nodeRef.getId());
                logger.debug("access_token: " + tokenInfo.getAccessToken());

                String loolBaseUrl = "";
                try {
                    loolBaseUrl = loolService.getWopiSrcURL(nodeRef, "edit");
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new AlfrescoRuntimeException(e.getMessage());
                }

                url = String.format(
                        loolBaseUrl + "permission=edit&WOPISrc=%s&access_token=%s",
                        commonUrl + "/alfresco/s/wopi/files/" + nodeRef.getId(),
                        tokenInfo.getAccessToken()
                );
            } else {
                throw new AlfrescoRuntimeException("URL generation error. Unknown file extension: " + extension);
            }
        } else {
            throw new AlfrescoRuntimeException("URL generation error. Unknown operation: " + operation);
        }
        logger.debug("url: " + url);
        return url;
    }

    public void setNodeRefUtil(NodeRefUtil nodeRefUtil) {
        this.nodeRefUtil = nodeRefUtil;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setLoolService(LOOLService loolService) {
        this.loolService = loolService;
    }
}
