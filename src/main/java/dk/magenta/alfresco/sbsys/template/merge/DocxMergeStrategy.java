package dk.magenta.alfresco.sbsys.template.merge;

import dk.magenta.alfresco.sbsys.template.json.Case;
import org.wickedsource.docxstamper.DocxStamper;
import org.wickedsource.docxstamper.DocxStamperConfiguration;

import java.io.InputStream;
import java.io.OutputStream;

public class DocxMergeStrategy implements MergeStrategy {

    @Override
    public void merge(Case sbsysCase, InputStream inputStream, OutputStream outputStream) {
        DocxStamper stamper = new DocxStamperConfiguration()
                .leaveEmptyOnExpressionError(true)
                .build();
        stamper.stamp(inputStream, sbsysCase, outputStream);
    }

}
