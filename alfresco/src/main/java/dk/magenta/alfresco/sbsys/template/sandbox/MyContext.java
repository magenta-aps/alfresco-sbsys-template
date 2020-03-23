package dk.magenta.alfresco.sbsys.template.sandbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyContext {

    private String name;
    private String foo;
    private SomePOJO somePOJO;

    private Map<String, String> map = new HashMap<>();
    private List<Map<String, String>> persons = new ArrayList<>();

    public MyContext(String name, String foo) {
        this.name = name;
        this.foo = foo;

        map.put("xyz", "hurra3");
        persons.add(map);
        somePOJO = new SomePOJO("This is s");
    }

    public List<Map<String, String>> getPersons() {
        return persons;
    }

    public void setPersons(List<Map<String, String>> persons) {
        this.persons = persons;
    }

    public SomePOJO getSomePOJO() {
        return somePOJO;
    }

    public void setSomePOJO(SomePOJO somePOJO) {
        this.somePOJO = somePOJO;
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
