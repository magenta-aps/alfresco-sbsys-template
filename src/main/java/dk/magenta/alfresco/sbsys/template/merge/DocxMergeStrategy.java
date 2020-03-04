package dk.magenta.alfresco.sbsys.template.merge;

import org.wickedsource.docxstamper.DocxStamper;
import org.wickedsource.docxstamper.DocxStamperConfiguration;

public class DocxMergeStrategy implements MergeStrategy {

    @Override
    public void merge(MergeDataWebscript mergeDataWebscript) {

        DocxStamper stamper = new DocxStamperConfiguration()
                .leaveEmptyOnExpressionError(true)
                .build();

        stamper.stamp(
                mergeDataWebscript.getInputStream(),
                mergeDataWebscript.getSbsysCase(),
                mergeDataWebscript.getOutputStream()
        );
    }
}
