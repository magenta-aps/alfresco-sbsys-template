package dk.magenta.alfresco.sbsys.template.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.webscripts.Format;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class RequestResponseHandler {

    private static Gson gson = new Gson();

    public static <T> T deserialize(String json, Class<T> clazz) throws JsonSyntaxException {
        return gson.fromJson(json, clazz);
    }

    public static String getJsonSyntaxErrorMessage() {
        Map<String, String> jsonErrorMessage = new HashMap<>();
        jsonErrorMessage.put("msg", "Could not parse JSON");
        return serialize(jsonErrorMessage);
    }

    public static String getMergeErrorMessage() {
        Map<String, String> mergeErrorMessage = new HashMap<>();
        mergeErrorMessage.put("msg", "Kunne ikke flette sagens data ned i skabelonen. " +
                "Kontrollér venligst syntaxen i skabelonen");
        return serialize(mergeErrorMessage);
    }

    public static String serialize(Object obj) {
        return gson.toJson(obj);
    }

    public static void writeWebscriptResponse(WebScriptResponse response, String resp) {
        try {
            response.setContentType(Format.JSON.mimetype());
            response.setContentEncoding("UTF-8");
            response.getWriter().write(resp);
        } catch (IOException e) {
            throw new AlfrescoRuntimeException(e.getMessage());
        }
    }
}