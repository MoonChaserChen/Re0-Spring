# Resource
Spring对资源的抽象，比如一个xml文件。
```java
ApplicationContext ctx =
    new ClassPathXmlApplicationContext("classpath*:conf/appContext.xml");
```

## ResourceLoader
用以加载资源。
```java
public interface ResourceLoader {
	String CLASSPATH_URL_PREFIX = "classpath:";
    
	Resource getResource(String location);

	ClassLoader getClassLoader();
}
```