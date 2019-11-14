package dk.magenta.alfresco.sbsys.template.platformsample;

import com.google.gson.Gson;

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
    }
}
