package dk.magenta.alfresco.sbsys.template.sandbox;

public class MyContext {

    private String name;
    private String foo;

    public MyContext(String name, String foo) {
        this.name = name;
        this.foo = foo;
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
}
