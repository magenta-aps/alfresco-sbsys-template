package dk.magenta.alfresco.sbsys.template;

import com.google.gson.Gson;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.site.SiteService;
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
    private SearchService searchService;
    private SiteService siteService;

    // TODO: get the siteShortName from alfresco-global.properties
    private static final String CONTAINER = "documentLibrary";
    private static final String SITE_SHORT_NAME = "swsdp";

    @Override
    public void execute(WebScriptRequest request, WebScriptResponse response) {

        // TODO: catch exceptions if site does not exists

        // Get the document library NodeRef of the relevant site
        PagingRequest pagingRequest = new PagingRequest(Integer.MAX_VALUE);
        PagingResults<FileInfo> containers = siteService.listContainers(SITE_SHORT_NAME, pagingRequest);
        NodeRef docLib = siteService.getContainer(SITE_SHORT_NAME, CONTAINER);

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

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
}
