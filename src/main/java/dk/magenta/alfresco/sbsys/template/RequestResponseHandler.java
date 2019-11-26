package dk.magenta.alfresco.sbsys.template;

import com.google.gson.Gson;
import org.alfresco.error.AlfrescoRuntimeException;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;

public class RequestResponseHandler {

    private static Gson gson = new Gson();

    public static <T> T deserialize(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static String serialize(Object obj) {
        return gson.toJson(obj);
    }

    public static void writeWebscriptResponse(WebScriptResponse response, String json) {
        try {
            response.getWriter().write(json);
        } catch (IOException e) {
            throw new AlfrescoRuntimeException(e.getMessage());
        }
    }
}
