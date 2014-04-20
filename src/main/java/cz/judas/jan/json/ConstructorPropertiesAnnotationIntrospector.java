package cz.judas.jan.json;

import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

import java.beans.ConstructorProperties;

public class ConstructorPropertiesAnnotationIntrospector extends JacksonAnnotationIntrospector {
    @Override
    public String findDeserializationName(AnnotatedParameter param) {
        String name = super.findDeserializationName(param);
        if(name == null) {
            AnnotatedWithParams owner = param.getOwner();
            if(owner instanceof AnnotatedConstructor && owner.hasAnnotation(ConstructorProperties.class)) {
                ConstructorProperties constructorProperties = owner.getAnnotation(ConstructorProperties.class);
                name = constructorProperties.value()[param.getIndex()];
            }
        }
        return name;
    }
}
