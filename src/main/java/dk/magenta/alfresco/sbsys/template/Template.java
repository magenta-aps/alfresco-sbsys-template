package dk.magenta.alfresco.sbsys.template;

import com.google.gson.Gson;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class Template extends AbstractWebScript {
    private static Log logger = LogFactory.getLog(Template.class);

    private FileFolderService fileFolderService;

    private Properties properties;
    private SiteService siteService;

    // TODO: move this string to common class
    private static final String CONTAINER = "documentLibrary";

    @Override
    public void execute(WebScriptRequest request, WebScriptResponse response) {

        // TODO: add debug log messages
        // TODO: catch exceptions if site does not exists

        // Get the document library NodeRef of the relevant site
        // TODO: make common method as this is also used in MergeData
        PagingRequest pagingRequest = new PagingRequest(Integer.MAX_VALUE);
        PagingResults<FileInfo> containers = siteService.listContainers(getSite(), pagingRequest);
        NodeRef docLib = siteService.getContainer(getSite(), CONTAINER);

        // Get the templates (files) in the docLib
        List<FileInfo> template_files = fileFolderService.listFiles(docLib);
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

    /**
     * Get site short name from alfresco-global.properties
     * @return Site short name
     */
    private String getSite() {
        return properties.getProperty("sbsys.template.site");
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
}
