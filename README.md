Jackson @ConstructorProperties
==============================

**IMPORTANT**: Since jackson-databind 2.9, this feature works out of the box and this add-on is no longer needed (see com.fasterxml.jackson.databind.MapperFeature.INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES).

This repository contains support for the [@java.beans.ConstructorProperties][1] annotation for [Jackson][2] version 2 and up to 2.8.

This feature is useful when you want Jackson te deserialize immutable objects. Names of constructor arguments are not retained in Java class files and therefore must be supplied using external methods, most often annotations.
In Jackson, each argument of the constructor must be annotated using a @JsonProperty proprietary annotation. This is quite verbose and moreover needlessly couples the value class to Jackson.

Since Java SE 6, the standard libraries provide an annotation just for this purpose - the @java.beans.ConstructorProperties. Unlike the @JsonProperty annotations, this one is put on the constructor itself, reducing clutter in the method signature.
This library adds support for the ConstructorProperties annotation to Jackson, allowing simplification of value classes and their de-coupling from Jackson.

Usage
-----

Registering the library in Jackson:

```java
        ObjectMapper objectMapper = new ObjectMapper()
                .setAnnotationIntrospector(new ConstructorPropertiesAnnotationIntrospector());
        objectMapper.readValue(json, TestObject.class);
```

Example
-----

Instead of writing

```java
public class TestObject {
    public TestObject(@JsonProperty("arg1") int arg1, @JsonProperty("arg2") String arg2) {
        ...
    }
}
```

You can now use

```java
public class TestObject {
    @ConstructorProperties("arg1", "arg2")
    public TestObject(int arg1, String arg2) {
        ...
    }
}
```

[1]: http://download.oracle.com/javase/6/docs/api/java/beans/ConstructorProperties.html
[2]: https://github.com/FasterXML/jackson