package dk.magenta.alfresco.sbsys.template.sandbox;

import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.field.VariableField;

import java.io.File;

public class OdtMerge {

    public static void main(String[] args) throws Exception {

        File in = new File("/tmp/simple.odt");

        // NOTE: using the File object works, but getResourceAsStream fails ?!
        // TextDocument odtDoc = TextDocument.loadDocument(OdtMerge.class.getResourceAsStream("/sandbox/test.odt"));
        TextDocument odtDoc = TextDocument.loadDocument(in);
//        OdfElement element = odtDoc.getVariableContainerElement();
        VariableField field = odtDoc.getVariableFieldByName("xyzz");
        field.updateField("hurra", null);

        File file = new File("/tmp/out.odt");

        odtDoc.save(file);

    }
}
