package cz.judas.jan.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import org.junit.Before;
import org.junit.Test;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ConstructorPropertiesAnnotationIntrospectorTest {
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        objectMapper = new ObjectMapper()
                .setAnnotationIntrospector(new ConstructorPropertiesAnnotationIntrospector());
    }

    @Test
    public void deserializesSimpleObject() throws Exception {
        String json = jsonFrom(map(
                "prop1", "xx",
                "prop2", 2
        ));

        ValueObject valueObject = objectMapper.readValue(json, ValueObject.class);

        assertThat(valueObject, is(equalTo(new ValueObject("xx", 2))));
    }

    @Test
    public void deserializesObjectContainingList() throws Exception {
        String json = jsonFrom(map(
                "prop1", "ee",
                "prop2", list(1.0, 2.5)
        ));

        ObjectWithListProperty valueObject = objectMapper.readValue(json, ObjectWithListProperty.class);

        assertThat(valueObject, is(equalTo(new ObjectWithListProperty("ee", list(1.0, 2.5)))));
    }

    @Test
    public void deserializesObjectContainingMap() throws Exception {
        String json = jsonFrom(map(
                "prop1", "gg",
                "prop2", map(9, "kio", 15, "fio")
        ));

        ObjectWithMapProperty valueObject = objectMapper.readValue(json, ObjectWithMapProperty.class);

        assertThat(valueObject, is(equalTo(new ObjectWithMapProperty("gg", map(9, "kio", 15, "fio")))));
    }

    @Test
    public void deserializesNestedObjects() throws Exception {
        Map<String, String> innerJson = map(
                "prop1", "ww",
                "prop2", list(5, 54)
        );
        String json = jsonFrom(map(
                "prop1", "kk",
                "prop2", innerJson
        ));

        ObjectWithNestedProperty valueObject = objectMapper.readValue(json, ObjectWithNestedProperty.class);

        assertThat(valueObject, is(equalTo(new ObjectWithNestedProperty("kk", new ObjectWithListProperty("ww", list(5.0, 54.0))))));
    }

    @Test(expected = JsonMappingException.class) // TODO why?
    public void failsOnObjectsWithMultipleAnnotatedConstructors() throws Exception {
        String json = jsonFrom(map("prop1", "po"));

        objectMapper.readValue(json, ObjectWithMultipleConstructors.class);
    }

    @Test
    public void usesCustomDeserializers() throws Exception {
        String json = jsonFrom(map("customField", "value"));

        ObjectWithCustomField value = objectMapper.readValue(json, ObjectWithCustomField.class);

        assertThat(value.customField, is("custom value"));
    }

    private static <T> List<T> list(T... items) {
        return Collections.unmodifiableList(Arrays.asList(items));
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Map<K, V> map(K key, V value, Object... rest) {
        Map<K, V> result = new LinkedHashMap<K, V>();
        result.put(key, value);
        int restLength = rest.length;
        if (restLength % 2 != 0) {
            throw new IllegalArgumentException("Number of arguments must be even (key/value pairs)");
        }
        for (int i = 0; i < restLength; i += 2) {
            result.put((K) rest[i], (V) rest[i + 1]);
        }
        return Collections.unmodifiableMap(result);
    }

    private static String jsonFrom(Object object) throws Exception {
        return new ObjectMapper().writeValueAsString(object);
    }

    private static class ValueObject {
        private final String prop1;
        private final int prop2;

        @ConstructorProperties({"prop1", "prop2"})
        private ValueObject(String prop1, int prop2) {
            this.prop1 = prop1;
            this.prop2 = prop2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

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

    private static class ObjectWithListProperty {
        private final String prop1;
        private final List<Double> prop2;

        @ConstructorProperties({"prop1", "prop2"})
        private ObjectWithListProperty(String prop1, List<Double> prop2) {
            this.prop1 = prop1;
            this.prop2 = prop2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ObjectWithListProperty that = (ObjectWithListProperty) o;

            return prop1.equals(that.prop1) && prop2.equals(that.prop2);
        }

        @Override
        public int hashCode() {
            int result = prop1.hashCode();
            result = 31 * result + prop2.hashCode();
            return result;
        }
    }

    private static class ObjectWithMapProperty {
        private final String prop1;
        private final Map<Integer, String> prop2;

        @ConstructorProperties({"prop1", "prop2"})
        private ObjectWithMapProperty(String prop1, Map<Integer, String> prop2) {
            this.prop1 = prop1;
            this.prop2 = prop2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

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

    private static class ObjectWithNestedProperty {
        private final String prop1;
        private final ObjectWithListProperty prop2;

        @ConstructorProperties({"prop1", "prop2"})
        private ObjectWithNestedProperty(String prop1, ObjectWithListProperty prop2) {
            this.prop1 = prop1;
            this.prop2 = prop2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

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

    @SuppressWarnings("UnusedDeclaration")
    private static class ObjectWithMultipleConstructors {
        private final String prop1;
        private final String prop2;

        @ConstructorProperties("prop1")
        private ObjectWithMultipleConstructors(String prop1) {
            this(prop1, "a");
        }

        @ConstructorProperties({"prop1", "prop2"})
        private ObjectWithMultipleConstructors(String prop1, String prop2) {
            this.prop1 = prop1;
            this.prop2 = prop2;
        }

        @Override
        public String toString() {
            return "ObjectWithMultipleConstructors{" +
                    "prop1='" + prop1 + '\'' +
                    ", prop2='" + prop2 + '\'' +
                    '}';
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    private static class ObjectWithCustomField {
        @JsonDeserialize(using = CustomDeserializer.class)
        private final String customField;

        @ConstructorProperties("customField")
        private ObjectWithCustomField(String customField) {
            this.customField = customField;
        }

        private static class CustomDeserializer extends StdScalarDeserializer<String> {
            private CustomDeserializer() {
                super(String.class);
            }

            @Override
            public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                return "custom " + jsonParser.getValueAsString();
            }
        }
    }

}
