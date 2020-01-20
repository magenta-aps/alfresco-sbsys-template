package dk.magenta.alfresco.sbsys.template.behavior;

import dk.magenta.alfresco.sbsys.template.HttpHandler;
import dk.magenta.alfresco.sbsys.template.MergeData;
import dk.magenta.alfresco.sbsys.template.NodeRefUtil;
import dk.magenta.alfresco.sbsys.template.json.MultipartRequest;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.version.VersionServicePolicies;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.Version;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class SbsysUploadBehavior implements VersionServicePolicies.AfterCreateVersionPolicy {

    private static Log logger = LogFactory.getLog(SbsysUploadBehavior.class);
    private static final String AFTER_CREATE_VERSION = "afterCreateVersion";

    private AttributeService attributeService;
    private NodeRefUtil nodeRefUtil;
    private PolicyComponent policyComponent;

    public void init() {
        policyComponent.bindClassBehaviour(
                VersionServicePolicies.AfterCreateVersionPolicy.QNAME,
                ContentModel.ASPECT_VERSIONABLE,
                new JavaBehaviour(
                        this,
                        AFTER_CREATE_VERSION,
                        Behaviour.NotificationFrequency.TRANSACTION_COMMIT
                )
        );
    }

    @Override
    public void afterCreateVersion(NodeRef versionableNode, Version version) {
        logger.debug("Called behavior");

        boolean shouldUpload = nodeRefUtil.shouldDocumentBeUploaded(versionableNode, version);
        logger.debug("shouldUpload = " + shouldUpload);

        if (shouldUpload) {

            logger.debug("versionalbleNode.toString() = " + versionableNode.toString());

            String sagId = (String) attributeService.getAttribute(versionableNode.toString(), MergeData.CASE_ID);
            String documentName = (String) attributeService.getAttribute(versionableNode.toString(), MergeData.DOCUMENT_NAME);
            logger.debug("Get token...");
            String token = (String) attributeService.getAttribute(versionableNode.toString(), MergeData.TOKEN);
            logger.debug("Got token...");
            Map<String, String> documentDetails = nodeRefUtil.getUploadDocumentDetails(versionableNode.toString());

            // NOTE: this is NOT a multipart/form-data request. It is a normal POST request to a
            // separate service that in turn will perform the actual multipart/form-data request

            MultipartRequest multipartRequest = new MultipartRequest(
                    Integer.parseInt(sagId),
                    documentName,
                    documentDetails.get("filename"),
                    documentDetails.get("mimeType"),
                    token,
                    documentDetails.get("contentStorePath")
            );

            String response = HttpHandler.POST_MULTIPART(multipartRequest);

            logger.debug("Document uploaded");

            nodeRefUtil.deleteNode(versionableNode.toString());
            attributeService.removeAttribute(versionableNode.toString(), MergeData.CASE_ID);
            attributeService.removeAttribute(versionableNode.toString(), MergeData.DOCUMENT_NAME);
            attributeService.removeAttribute(versionableNode.toString(), MergeData.TOKEN + "1");
            attributeService.removeAttribute(versionableNode.toString(), MergeData.TOKEN + "2");
            attributeService.removeAttribute(versionableNode.toString(), MergeData.TOKEN + "3");

            logger.debug("Final template document deleted");
        }
    }

    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    public void setNodeRefUtil(NodeRefUtil nodeRefUtil) {
        this.nodeRefUtil = nodeRefUtil;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }
}
