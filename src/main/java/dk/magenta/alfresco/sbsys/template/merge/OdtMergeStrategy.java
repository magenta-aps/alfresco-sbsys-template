package dk.magenta.alfresco.sbsys.template.merge;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class OdtMergeStrategy implements MergeStrategy {

    private static Log logger = LogFactory.getLog(OdtMergeStrategy.class);

    @Override
    public void merge(MergeDataWebscript mergeDataWebscript) {

        logger.debug("LoolMergeStrategy called");

        Gson gson = new Gson();

        JsonElement root = gson.toJsonTree(mergeDataWebscript.getSbsysCase());
        Map<String, String> caseFields = new TreeMap<>();

        if (root.isJsonObject()) {
            JsonObject caseObj = root.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = caseObj.entrySet();
            entries.forEach(entry -> {
                List<String> keys = new ArrayList<>();
                keys.add(entry.getKey());
                extractCaseFields(entry.getValue(), caseFields, keys);
            });
        } else {
            throw new AlfrescoRuntimeException("Merge error: root element not a JSON object");
        }

        caseFields.forEach((key, value) -> System.out.println(key + " " + value));

    }

    private void extractCaseFields(JsonElement element, Map<String, String> caseFields, final List<String> keys) {
        if (element.isJsonNull()) {
            caseFields.put(String.join("_", keys), "");
        } else if (element.isJsonPrimitive()) {
            caseFields.put(String.join("_", keys), element.getAsString());
        } else if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();

            entries.forEach(entry -> {
                keys.add(entry.getKey());
                extractCaseFields(entry.getValue(), caseFields, keys);
            });
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                keys.add(Integer.toString(i));
                extractCaseFields(array.get(i), caseFields, keys);
            }
        }

        keys.remove(keys.size() - 1);
    }
}
