package cz.judas.jan.json.testclasses;

import java.beans.ConstructorProperties;

@SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal"})
public class ObjectWithMultipleConstructors {
    private final String prop1;
    private final String prop2;

    @ConstructorProperties("prop1")
    public ObjectWithMultipleConstructors(String prop1) {
        this(prop1, "a");
    }

    @ConstructorProperties({"prop1", "prop2"})
    public ObjectWithMultipleConstructors(String prop1, String prop2) {
        this.prop1 = prop1;
        this.prop2 = prop2;
    }
}
