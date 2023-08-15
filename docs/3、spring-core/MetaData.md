# MetaData
不是一个具体的接口，而是表示元数据的一个系列。包括以下部分：
```plantuml
interface ClassMetadata
interface AnnotatedTypeMetadata
interface AnnotationMetadata
interface MethodMetadata

ClassMetadata <|-- AnnotationMetadata
AnnotatedTypeMetadata <|-- AnnotationMetadata
AnnotatedTypeMetadata <|-- MethodMetadata
ClassMetadata <|-- StandardClassMetadata
StandardClassMetadata <|-- StandardAnnotationMetadata
AnnotationMetadata <|-- StandardAnnotationMetadata
AnnotationMetadata <|-- SimpleAnnotationMetadata
MethodMetadata <|-- StandardMethodMetadata
MethodMetadata <|-- SimpleMethodMetadata
```

## ClassMetadata
类的元数据信息。通过 Java Class类也能获取到，这个接口的设计在于不用进行类加载就能获取到这些信息。包括以下部分：

| 方法                                  | 说明                      |
|-------------------------------------|-------------------------|
| String getClassName();              | 获取类名                    |
| boolean isInterface();              | 是否是接口                   |
| boolean isAnnotation();             | 是否是注解                   |
| boolean isAbstract();               | 是否是抽象类                  |
| default boolean isConcrete()        | 是否是实体类（非抽象类&接口）         |
| boolean isFinal();                  | 是否是final类               |
| boolean isIndependent();            | 是否独立：内部类不独立；外部类及静态内部类独立 |
| String getEnclosingClassName();     | 获取外部类（针对于内部类而言）         |
| default boolean hasEnclosingClass() | 是否有外部类                  |
| String getSuperClassName();         | 获取父类                    |
| default boolean hasSuperClass()     | 是否有父类                   |
| String[] getInterfaceNames();       | 获取当前类实现的接口              |
| String[] getMemberClassNames();     | ？？？                     |

## AnnotatedTypeMetadata
获取注解信息。包括类上的注解（AnnotationMetadata）以及方法上的注解（MethodMetadata）。其核心方法是：
```java
public interface AnnotatedTypeMetadata {
    /**
     * 核心方法
     */
    MergedAnnotations getAnnotations();
    // ------------------
    // other code ignored
    // ------------------
}
```

## AnnotationMetadata
```java
public interface AnnotationMetadata extends ClassMetadata, AnnotatedTypeMetadata {
    /**
     * 对类中注解方法的获取
     * @param annotationName
     * @return
     */
    Set<MethodMetadata> getAnnotatedMethods(String annotationName);
    // ------------------
    // other code ignored
    // ------------------
}
```

## MethodMetadata
方法的元数据信息。通过 Java Method类也能获取到，这个接口的设计在于不用进行类加载就能获取到这些信息。包括以下部分：

| 方法                              | 说明                                                        |
|---------------------------------|-----------------------------------------------------------|
| String getMethodName();         | 获取方法名                                                     |
| String getDeclaringClassName(); | 获取类名                                                      |
| String getReturnTypeName();     | 获取返回值类型                                                   |
| boolean isAbstract();           | 是否是抽象方法                                                   |
| boolean isStatic();             | 是否是静态方法                                                   |
| boolean isFinal();              | 是否是final方法                                                |
| boolean isOverridable();        | 是否可被override。i.e. not marked as static, final, or private |

## 具体实现 
StandardXxxMetadata: 以反射实现（StandardClassMetadata、StandardAnnotationMetadata、StandardMethodMetadata）  
SimpleXxxMetadata: 以POJO实现（SimpleAnnotationMetadata、SimpleMethodMetadata）

