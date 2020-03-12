package dk.magenta.alfresco.sbsys.template.upload;

import dk.magenta.alfresco.sbsys.template.Constants;
import dk.magenta.alfresco.sbsys.template.http.RequestResponseHandler;
import dk.magenta.alfresco.sbsys.template.json.Upload;
import org.alfresco.service.cmr.attributes.AttributeService;

import java.util.HashMap;
import java.util.Map;

public class MergeJsonBuilderStrategy implements  JsonBuilderStrategy {

    @Override
    public String build(UploadDocument uploadDocument) {
        Upload req = uploadDocument.getReq();
        AttributeService attributeService = uploadDocument.getAttributeService();

        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("SagID", (String) attributeService.getAttribute(req.getPreUploadId(), Constants.CASE_ID));
        jsonMap.put("Navn", (String) attributeService.getAttribute(req.getPreUploadId(), Constants.DOCUMENT_NAME));

        return RequestResponseHandler.serialize(jsonMap);
    }
}
