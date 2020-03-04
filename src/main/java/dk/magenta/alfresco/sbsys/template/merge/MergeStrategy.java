package dk.magenta.alfresco.sbsys.template.merge;

import dk.magenta.alfresco.sbsys.template.json.Case;

import java.io.InputStream;
import java.io.OutputStream;

public interface MergeStrategy {
    void merge(Case sbsysCase, InputStream inputStream, OutputStream outputStream);
}
