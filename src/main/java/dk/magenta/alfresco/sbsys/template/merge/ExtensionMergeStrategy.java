package dk.magenta.alfresco.sbsys.template.merge;

import org.alfresco.error.AlfrescoRuntimeException;

public class ExtensionMergeStrategy implements MergeStrategy {

    private MergeStrategy docxMergeStrategy;
    private MergeStrategy odtMergeStrategy;

    private static final String DOCX = ".docx";
    private static final String ODT = ".odt";

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
