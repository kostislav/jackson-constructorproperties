package com.judas.jan.json;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ConstructorPropertiesValueInstantiator extends ValueInstantiator {
    private final Class<?> clazz;
    private final Constructor<?> constructor;
    private final SettableBeanProperty[] settableBeanProperties;

    public ConstructorPropertiesValueInstantiator(Class<?> clazz, Constructor<?> constructor, SettableBeanProperty[] settableBeanProperties) {
        this.clazz = clazz;
        this.constructor = constructor;
        this.settableBeanProperties = settableBeanProperties.clone();
    }

    @Override
    public String getValueTypeDesc() {
        return clazz.getName();
    }

    @Override
    public boolean canCreateFromObjectWith() {
        return true;
    }

    @Override
    public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
        return settableBeanProperties.clone();
    }

    @Override
    public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) throws JsonMappingException {
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new JsonMappingException("Could not instantiate object", e);
        }
    }
}
