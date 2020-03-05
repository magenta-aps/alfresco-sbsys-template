package dk.magenta.alfresco.sbsys.template.merge;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
                extractCaseFields(entry.getValue(), caseFields, new StringBuilder(entry.getKey()));
            });
        } else {
            throw new AlfrescoRuntimeException("Merge error: root element not a JSON object");
        }


        caseFields.forEach((key, value) -> System.out.println(key + " " + value));



    }

    private void extractCaseFields(JsonElement element, Map<String, String> caseFields, final StringBuilder keyBuilder) {
        if (element.isJsonNull()) {
            caseFields.put(keyBuilder.toString(), "");
            keyBuilder.remove_last_append
        } else if (element.isJsonPrimitive()) {
            // TODO: test id getAsString works
            caseFields.put(keyBuilder.toString(), element.getAsString());
        } else if (element.isJsonObject()) {
            keyBuilder.append("_");

            JsonObject obj = element.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();

            entries.forEach(entry -> {
                keyBuilder.append(entry.getKey());
                extractCaseFields(entry.getValue(), caseFields, keyBuilder);
            });
        } else if (element.isJsonArray()) {
            keyBuilder.append("_");

            JsonArray array = element.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                keyBuilder.append(i);
                extractCaseFields(array.get(i), caseFields, keyBuilder);
            }
        }
    }
}
