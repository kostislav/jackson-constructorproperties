package com.judas.jan.json;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.CreatorProperty;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.ValueInstantiators;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.beans.ConstructorProperties;
import java.util.*;

public class ConstructorPropertiesValueInstantiators extends ValueInstantiators.Base {
    private final Map<Class<?>, ConstructorPropertiesValueInstantiator> cache = new HashMap<>();

    @Override
    public ValueInstantiator findValueInstantiator(DeserializationConfig config, BeanDescription beanDesc, ValueInstantiator defaultInstantiator) {
        JavaType type = beanDesc.getType();
        Class<?> clazz = type.getRawClass();
        List<AnnotatedConstructor> constructors = beanDesc.getConstructors();

        synchronized (cache) {
            ConstructorPropertiesValueInstantiator instantiator = cache.get(clazz);
            if(instantiator != null) {
                return instantiator;
            }

            Set<AnnotatedConstructor> annotatedConstructors = findAnnotatedConstructors(constructors);
            if(annotatedConstructors.isEmpty()) {
                return defaultInstantiator;
            } else if(annotatedConstructors.size() > 1) {
                throw new IllegalArgumentException("More than one annotated constructor found");
            } else {
                AnnotatedConstructor constructor = annotatedConstructors.iterator().next();
                SettableBeanProperty[] settableBeanProperties = extractBeanProperties(constructor, config.getTypeFactory());
                instantiator = new ConstructorPropertiesValueInstantiator(clazz, constructor.getAnnotated(), settableBeanProperties);
                cache.put(clazz, instantiator);
                return instantiator;
            }
        }
    }

    private Set<AnnotatedConstructor> findAnnotatedConstructors(List<AnnotatedConstructor> constructors) {
        Set<AnnotatedConstructor> annotatedConstructors = new HashSet<>();
        for (AnnotatedConstructor constructor : constructors) {
            if(constructor.hasAnnotation(ConstructorProperties.class)) {
                annotatedConstructors.add(constructor);
            }
        }
        return annotatedConstructors;
    }

    private SettableBeanProperty[] extractBeanProperties(AnnotatedConstructor constructor, TypeFactory typeFactory) {
        ConstructorProperties constructorProperties = constructor.getAnnotation(ConstructorProperties.class);
        String[] parameterNames = constructorProperties.value();
        int numParams = constructor.getParameterCount();
        SettableBeanProperty[] settableBeanProperties = new SettableBeanProperty[numParams];
        for (int i = 0; i < numParams; i++) {
            settableBeanProperties[i] = new CreatorProperty(
                    new PropertyName(parameterNames[i]),
                    typeFactory.constructType(constructor.getGenericParameterType(i)),
                    null,
                    null,
                    null,
                    constructor.getParameter(i),
                    i,
                    null,
                    PropertyMetadata.construct(true, null)
            );
        }
        return settableBeanProperties;
    }
}
