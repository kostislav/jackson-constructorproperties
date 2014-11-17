package cz.judas.jan.json.testclasses;

import java.beans.ConstructorProperties;
import java.util.Map;

public class ObjectWithMapProperty {
    private final String prop1;
    private final Map<Integer, String> prop2;

    @ConstructorProperties({"prop1", "prop2"})
    public ObjectWithMapProperty(String prop1, Map<Integer, String> prop2) {
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

        ObjectWithMapProperty that = (ObjectWithMapProperty) o;

        return prop1.equals(that.prop1) && prop2.equals(that.prop2);
    }

    @Override
    public int hashCode() {
        int result = prop1.hashCode();
        result = 31 * result + prop2.hashCode();
        return result;
    }
}
