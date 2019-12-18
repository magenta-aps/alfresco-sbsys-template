package dk.magenta.alfresco.sbsys.template.sandbox;

import com.google.gson.Gson;
import dk.magenta.alfresco.sbsys.template.json.Merge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sandbox {
    public static void main(String[] args) {
        System.out.println("hurra");

        Map<String, String> map = new HashMap<>();
        map.put("x", "y");

        List<Map<String, String>> list = new ArrayList<>();
        list.add(map);

        Gson gson = new Gson();
        String json = gson.toJson(list);

        System.out.println(json);

        String post = "{\n" +
                "  \"id\": \"458514b2-692c-4aff-b75a-8d667ba111f5\",\n" +
                "  \"kladde\": {\n" +
                "    \"SagID\": \"6\",\n" +
                "    \"Navn\": \"testtitel\"\n" +
                "  },\n" +
                "  \"urls\": {\n" +
                "    \"opretkladde\": \"https://<server:port>/convergens-sbsip-sbsys-webapi-proxy/proxy/api/kladde\",\n" +
                "    \"part\": \"https://<server:port>/convergens-sbsip-sbsys-webapi-proxy/proxy/api/part/person?id=1\",\n" +
                "    \"sag\": \"https://<server:port>/convergens-sbsip-sbsys-webapi-proxy/proxy/api/sag/12344\",\n" +
                "    \"redirect\": \"https://<server:port>/p-sag/v1/#/sager/12344/kladder/{kladdeId}\"\n" +
                "  },\n" +
                "  \"token\": {\n" +
                "    \"refreshToken\": \"eyJhbGciOiJSUzI1NiIs...nXM4Yd5hg\",\n" +
                "    \"token\": \"eyJhbGciOiJSUzI1NiIsInR5.....EnwYwsJnoB5tz18neg\",\n" +
                "    \"exp\": 1558515632,\n" +
                "    \"refreshurl\": \"https://<server:port>/auth/realms/sbsip/protocol/openid-connect/token\",\n" +
                "    \"refreshuser\": \"username\",\n" +
                "    \"refreshpwd\": \"pwd\"\n" +
                "  }\n" +
                "}\n";

                Merge trm = gson.fromJson(post, Merge.class);
                System.out.println("hurra");
    }
}
