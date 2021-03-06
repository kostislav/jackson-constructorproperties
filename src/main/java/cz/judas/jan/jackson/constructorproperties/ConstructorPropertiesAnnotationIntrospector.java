package cz.judas.jan.jackson.constructorproperties;

import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.*;

import java.beans.ConstructorProperties;

public class ConstructorPropertiesAnnotationIntrospector extends JacksonAnnotationIntrospector {
    @Override
    public PropertyName findNameForDeserialization(Annotated a) {
        PropertyName name = super.findNameForDeserialization(a);
        if(name == null && a instanceof AnnotatedParameter) {
            AnnotatedParameter annotatedParameter = (AnnotatedParameter) a;
            AnnotatedWithParams owner = annotatedParameter.getOwner();
            if(isOwnerAnAnnotatedConstructor(owner)) {
                ConstructorProperties constructorProperties = owner.getAnnotation(ConstructorProperties.class);
                name = new PropertyName(constructorProperties.value()[annotatedParameter.getIndex()]);
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
