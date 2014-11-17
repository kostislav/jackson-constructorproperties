package cz.judas.jan.jackson.constructorproperties.testclasses;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

import java.beans.ConstructorProperties;
import java.io.IOException;

@SuppressWarnings("UnusedDeclaration")
public class ObjectWithCustomField {
    @JsonDeserialize(using = CustomDeserializer.class)
    private final String customField;

    @ConstructorProperties("customField")
    public ObjectWithCustomField(String customField) {
        this.customField = customField;
    }

    public String getCustomField() {
        return customField;
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
