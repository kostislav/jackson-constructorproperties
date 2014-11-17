package cz.judas.jan.jackson.constructorproperties.testclasses;

import java.beans.ConstructorProperties;

public class ValueObject {
    private final String prop1;
    private final int prop2;

    @ConstructorProperties({"prop1", "prop2"})
    public ValueObject(String prop1, int prop2) {
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

        ValueObject that = (ValueObject) o;

        return prop2 == that.prop2 && prop1.equals(that.prop1);
    }

    @Override
    public int hashCode() {
        int result = prop1.hashCode();
        result = 31 * result + prop2;
        return result;
    }
}
