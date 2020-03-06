package dk.magenta.alfresco.sbsys.template.merge;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.magenta.alfresco.sbsys.template.json.Case;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class OdtMergeStrategy implements MergeStrategy {

    private static Log logger = LogFactory.getLog(OdtMergeStrategy.class);

    @Override
    public void merge(MergeDataWebscript mergeDataWebscript) {

        logger.debug("LoolMergeStrategy called");

        Map<String, String> caseFields = flattenCase(mergeDataWebscript.getSbsysCase());

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

        if (!keys.isEmpty()) {
            keys.remove(keys.size() - 1);
        }
    }

    /**
     * Convert an SBSYS case (JSON) object representation into a flat map of
     * keys and values
     */
    private Map<String, String> flattenCase(Case sbsysCase) {
        Gson gson = new Gson();

        JsonElement root = gson.toJsonTree(sbsysCase);
        Map<String, String> caseFields = new TreeMap<>();
        List<String> keys = new ArrayList<>();
        extractCaseFields(root, caseFields, keys);

        return caseFields;
    }
}
