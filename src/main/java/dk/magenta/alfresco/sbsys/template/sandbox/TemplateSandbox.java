package dk.magenta.alfresco.sbsys.template.sandbox;

import org.wickedsource.docxstamper.DocxStamper;
import org.wickedsource.docxstamper.DocxStamperConfiguration;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class TemplateSandbox {

    public static void main(String[] args) throws Exception {

        MyContext mycontext = new MyContext("hurra1", "hurra2");
        InputStream template = TemplateSandbox.class.getResourceAsStream("/sandbox/ExpressionReplacementInGlobalParagraphsTest.docx");
        OutputStream out = new BufferedOutputStream(new FileOutputStream("/tmp/out.docx"));

        DocxStamper stamper = new DocxStamperConfiguration().build();
        stamper.stamp(template, mycontext, out);

        out.close();

    }

}
