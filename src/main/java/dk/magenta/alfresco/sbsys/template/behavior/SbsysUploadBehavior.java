package dk.magenta.alfresco.sbsys.template.behavior;

import dk.magenta.alfresco.sbsys.template.NodeRefUtil;
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
