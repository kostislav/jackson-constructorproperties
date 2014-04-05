package cz.judas.jan.json;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.CreatorProperty;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.ValueInstantiators;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.beans.ConstructorProperties;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConstructorPropertiesValueInstantiators extends ValueInstantiators.Base {
    private final Map<Class<?>, ConstructorPropertiesValueInstantiator> cache = new ConcurrentHashMap<Class<?>, ConstructorPropertiesValueInstantiator>();

    @Override
    public ValueInstantiator findValueInstantiator(DeserializationConfig config, BeanDescription beanDesc, ValueInstantiator defaultInstantiator) {
        JavaType type = beanDesc.getType();
        Class<?> clazz = type.getRawClass();
        List<AnnotatedConstructor> constructors = beanDesc.getConstructors();

        ConstructorPropertiesValueInstantiator instantiator = cache.get(clazz);
        if (instantiator != null) {
            return instantiator;
        }

        Set<AnnotatedConstructor> annotatedConstructors = findAnnotatedConstructors(constructors);

        if (annotatedConstructors.isEmpty()) {
            return defaultInstantiator;
        } else if (annotatedConstructors.size() > 1) {
            throw new IllegalArgumentException("More than one annotated constructor found");
        } else {
            instantiator = createInstantiator(config, annotatedConstructors);
            cache.put(clazz, instantiator);
            return instantiator;
        }
    }

    private static ConstructorPropertiesValueInstantiator createInstantiator(DeserializationConfig config, Set<AnnotatedConstructor> annotatedConstructors) {
        ConstructorPropertiesValueInstantiator instantiator;
        AnnotatedConstructor constructor = annotatedConstructors.iterator().next();
        SettableBeanProperty[] settableBeanProperties = extractBeanProperties(constructor, config.getTypeFactory());
        instantiator = new ConstructorPropertiesValueInstantiator(constructor.getAnnotated(), settableBeanProperties);
        return instantiator;
    }

    private static Set<AnnotatedConstructor> findAnnotatedConstructors(List<AnnotatedConstructor> constructors) {
        Set<AnnotatedConstructor> annotatedConstructors = new HashSet<AnnotatedConstructor>();
        for (AnnotatedConstructor constructor : constructors) {
            if (constructor.hasAnnotation(ConstructorProperties.class)) {
                annotatedConstructors.add(constructor);
            }
        }
        return annotatedConstructors;
    }

    private static SettableBeanProperty[] extractBeanProperties(AnnotatedConstructor constructor, TypeFactory typeFactory) {
        ConstructorProperties constructorProperties = constructor.getAnnotation(ConstructorProperties.class);
        String[] parameterNames = constructorProperties.value();
        int numParams = constructor.getParameterCount();
        SettableBeanProperty[] settableBeanProperties = new SettableBeanProperty[numParams];
        for (int i = 0; i < numParams; i++) {
            String parameterName = parameterNames[i];
            AnnotatedParameter parameter = constructor.getParameter(i);
            settableBeanProperties[i] = settableBeanProperty(typeFactory, i, parameterName, parameter);
        }
        return settableBeanProperties;
    }

    private static CreatorProperty settableBeanProperty(TypeFactory typeFactory, int i, String parameterName, AnnotatedParameter parameter) {
        return new CreatorProperty(
                new PropertyName(parameterName),
                typeFactory.constructType(parameter.getGenericType()),
                null,
                null,
                null,
                parameter,
                i,
                null,
                PropertyMetadata.construct(true, null)
        );
    }
}
