package cz.judas.jan.json;

import com.fasterxml.jackson.databind.introspect.*;

import java.beans.ConstructorProperties;

public class ConstructorPropertiesAnnotationIntrospector extends JacksonAnnotationIntrospector {
    @Override
    public String findDeserializationName(AnnotatedParameter param) {
        String name = super.findDeserializationName(param);
        if(name == null) {
            AnnotatedWithParams owner = param.getOwner();
            if(isOwnerAnAnnotatedConstructor(owner)) {
                ConstructorProperties constructorProperties = owner.getAnnotation(ConstructorProperties.class);
                name = constructorProperties.value()[param.getIndex()];
            }
        }
        return name;
    }

    @Override
    public Boolean hasRequiredMarker(AnnotatedMember m) {
        if(m instanceof AnnotatedParameter) {
            AnnotatedParameter annotatedParameter = (AnnotatedParameter) m;
            if(isOwnerAnAnnotatedConstructor(annotatedParameter.getOwner())) {
                return true;
            }
        }
        return super.hasRequiredMarker(m);
    }

    private static boolean isOwnerAnAnnotatedConstructor(AnnotatedWithParams owner) {
        return owner instanceof AnnotatedConstructor && owner.hasAnnotation(ConstructorProperties.class);
    }
}
