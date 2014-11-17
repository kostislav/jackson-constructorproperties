package cz.judas.jan.jackson.constructorproperties.testclasses;

import java.beans.ConstructorProperties;

public class ObjectWithNestedProperty {
    private final String prop1;
    private final ObjectWithListProperty prop2;

    @ConstructorProperties({"prop1", "prop2"})
    public ObjectWithNestedProperty(String prop1, ObjectWithListProperty prop2) {
        this.prop1 = prop1;
        this.prop2 = prop2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ObjectWithNestedProperty that = (ObjectWithNestedProperty) o;

        return prop1.equals(that.prop1) && prop2.equals(that.prop2);
    }

    @Override
    public int hashCode() {
        int result = prop1.hashCode();
        result = 31 * result + prop2.hashCode();
        return result;
    }
}
