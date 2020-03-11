package dk.magenta.alfresco.sbsys.template.merge;

import org.alfresco.error.AlfrescoRuntimeException;

import static dk.magenta.alfresco.sbsys.template.Constants.DOCX;
import static dk.magenta.alfresco.sbsys.template.Constants.ODT;

public class ExtensionMergeStrategy implements MergeStrategy {

    private MergeStrategy docxMergeStrategy;
    private MergeStrategy odtMergeStrategy;

    @Override
    public void merge(MergeDataWebscript mergeDataWebscript) {

        // State design pattern

        String extension = mergeDataWebscript.getMimetypeExtension().getSecond();
        if (extension.equals(DOCX)) {
            docxMergeStrategy.merge(mergeDataWebscript);
        } else if (extension.equals(ODT)) {
            odtMergeStrategy.merge(mergeDataWebscript);
        } else {
            throw new AlfrescoRuntimeException("Merge error: cannot merge data into " + extension + " documents");
        }
    }

    public void setDocxMergeStrategy(MergeStrategy docxMergeStrategy) {
        this.docxMergeStrategy = docxMergeStrategy;
    }

    public void setOdtMergeStrategy(MergeStrategy odtMergeStrategy) {
        this.odtMergeStrategy = odtMergeStrategy;
    }
}
