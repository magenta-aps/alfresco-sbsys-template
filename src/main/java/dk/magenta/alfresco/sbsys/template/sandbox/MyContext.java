package dk.magenta.alfresco.sbsys.template.sandbox;

import java.util.HashMap;
import java.util.Map;

public class MyContext {

    private String name;
    private String foo;

    private Map<String, String> map = new HashMap<>();

    public MyContext(String name, String foo) {
        this.name = name;
        this.foo = foo;

        map.put("xyz", "hurra3");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}
