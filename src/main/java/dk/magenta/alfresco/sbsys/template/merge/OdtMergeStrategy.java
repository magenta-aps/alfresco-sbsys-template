package dk.magenta.alfresco.sbsys.template.merge;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OdtMergeStrategy implements MergeStrategy {

    private static Log logger = LogFactory.getLog(OdtMergeStrategy.class);

    @Override
    public void merge(MergeDataWebscript mergeDataWebscript) {
        logger.debug("LoolMergeStrategy called");
    }
}
