package dk.magenta.alfresco.sbsys.template;

import com.google.gson.Gson;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Template extends AbstractWebScript {

    private static Log logger = LogFactory.getLog(Template.class);

    private FileFolderService fileFolderService;
    private NodeRefUtil nodeRefUtil;

    public Template(NodeRefUtil nodeRefUtil) {
        this.nodeRefUtil = nodeRefUtil;
    }

    @Override
    public void execute(WebScriptRequest request, WebScriptResponse response) {

        // TODO: add debug log messages
        // TODO: catch exceptions if site does not exists

        // Get the templates (files) in the docLib
        List<FileInfo> template_files = fileFolderService.listFiles(nodeRefUtil.getDocLib());
        List<Map<String, String>> templates = template_files.stream().map((FileInfo fileInfo) -> {
            Map<String, String> template = new HashMap<>();
            template.put("filename", fileInfo.getName());
            template.put("id", fileInfo.getNodeRef().getId());
            return template;
        }).collect(Collectors.toList());

        Gson gson = new Gson();
        String result = gson.toJson(templates);

        try {
            response.getWriter().write(result);
        } catch (IOException e) {
            throw new AlfrescoRuntimeException(e.getMessage());
        }
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }
}
