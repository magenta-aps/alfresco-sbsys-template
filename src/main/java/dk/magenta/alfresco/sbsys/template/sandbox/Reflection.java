package dk.magenta.alfresco.sbsys.template.sandbox;

import dk.magenta.alfresco.sbsys.template.json.Case;

import java.lang.reflect.Field;

public class Reflection {
    public static void main(String[] args) {
        Case sbsysCase = new Case();

        Class cls = sbsysCase.getClass();
        Field[] fields = cls.getDeclaredFields();

        Field personer = fields[1];
        System.out.println("type = " + personer.getType());

        System.out.println("Hurra");
    }
}
