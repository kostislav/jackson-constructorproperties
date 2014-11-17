package cz.judas.jan.json;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.judas.jan.json.testclasses.*;
import org.junit.Before;
import org.junit.Test;

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

        assertThat(value.getCustomField(), is("custom value"));
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
}
