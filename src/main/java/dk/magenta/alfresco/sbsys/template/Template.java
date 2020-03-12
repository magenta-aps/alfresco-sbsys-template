package dk.magenta.alfresco.sbsys.template;

import dk.magenta.alfresco.sbsys.template.http.RequestResponseHandler;
import dk.magenta.alfresco.sbsys.template.utils.NodeRefUtil;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Template extends AbstractWebScript {

    private static Log logger = LogFactory.getLog(Template.class);

    private FileFolderService fileFolderService;
    private NodeRefUtil nodeRefUtil;

    @Override
    public void execute(WebScriptRequest request, WebScriptResponse response) {

        logger.debug("GET /alfresco/s/template");

        // Get the templates (files) in the docLib
        List<FileInfo> template_files = fileFolderService.listFiles(nodeRefUtil.getDocLib());
        List<Map<String, String>> templates = template_files.stream().map((FileInfo fileInfo) -> {
            Map<String, String> template = new HashMap<>();
            template.put("filename", fileInfo.getName());
            template.put("id", fileInfo.getNodeRef().getId());
            return template;
        }).collect(Collectors.toList());

        String json = RequestResponseHandler.serialize(templates);
        RequestResponseHandler.writeWebscriptResponse(response, json);
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    public void setNodeRefUtil(NodeRefUtil nodeRefUtil) {
        this.nodeRefUtil = nodeRefUtil;
    }
}
