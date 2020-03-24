package dk.magenta.alfresco.sbsys.template.delete;

import dk.magenta.alfresco.sbsys.template.http.RequestResponseHandler;
import dk.magenta.alfresco.sbsys.template.utils.NodeRefUtil;
import org.alfresco.service.cmr.lock.UnableToReleaseLockException;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;

import static dk.magenta.alfresco.sbsys.template.utils.Constants.COULD_NOT_DELETE_NODE;
import static dk.magenta.alfresco.sbsys.template.utils.Constants.WORKSPACE_SPACESSTORE;

public class DeleteWebscript extends AbstractWebScript {

    private static final String NODE_ID = "nodeId";

    private NodeRefUtil nodeRefUtil;

    @Override
    public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) throws IOException {
        String nodeId = webScriptRequest.getServiceMatch().getTemplateVars().get(NODE_ID);

        // Unlock and delete node
        try {
            nodeRefUtil.deleteNode(WORKSPACE_SPACESSTORE + nodeId);
            webScriptResponse.setStatus(HttpStatus.SC_NO_CONTENT);
        } catch (InvalidNodeRefException e) {
            webScriptResponse.setStatus(HttpStatus.SC_NOT_FOUND);
            writeErrorResponse(webScriptResponse, e.getMessage());
        } catch (UnableToReleaseLockException e) {
            webScriptResponse.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            writeErrorResponse(webScriptResponse, e.getMessage());
        }
    }

    public void setNodeRefUtil(NodeRefUtil nodeRefUtil) {
        this.nodeRefUtil = nodeRefUtil;
    }

    private void writeErrorResponse(WebScriptResponse webScriptResponse, String msg) {
        RequestResponseHandler.writeWebscriptResponse(
                webScriptResponse,
                RequestResponseHandler.getJsonMessage(COULD_NOT_DELETE_NODE + msg)
        );
    }
}
