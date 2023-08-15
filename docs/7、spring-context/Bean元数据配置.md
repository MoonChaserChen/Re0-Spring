# Bean元数据配置
Bean的元数据配置告诉了Spring如何来创建Bean。Spring IoC容器本身与这种配置元数据的实际编写格式是完全解耦的，常用的有以下几种方式：
1. 基于XML的元数据配置。对应于 `ClassPathXmlApplicationContext`、`FileSystemXmlApplicationContext`
2. 基于Java的元数据配置。对应于 `AnnotationConfigApplicationContext`
3. 基于Groovy Bean Definition DSL的元数据配置。对应于 `GenericGroovyApplicationContext`

## 基于XML的元数据配置
使用 `ClassPathXmlApplicationContext` 或 `FileSystemXmlApplicationContext` 去解析这些配置，是ApplicationContext的一个实现。

可以通过提供少量的 XML 配置来指示容器使用 Java 注解或代码作为元数据格式。

### Bean的实例化方式配置
#### 用构造函数进行实例化
```xml
<bean id="exampleBean" class="examples.ExampleBean"/>
```
#### 用静态工厂方法进行实例化
```java
public class ClientService {
    private static ClientService clientService = new ClientService();
    private ClientService() {}

    public static ClientService createInstance() {
        return clientService;
    }
}
```
```xml
<bean id="clientService"
    class="examples.ClientService"
    factory-method="createInstance"/>
```
#### 用实例工厂方法进行实例化
```java
public class DefaultServiceLocator {

    private static ClientService clientService = new ClientServiceImpl();

    public ClientService createClientServiceInstance() {
        return clientService;
    }
}
```
```xml
<bean id="serviceLocator" class="examples.DefaultServiceLocator"/>

<bean id="clientService"
    factory-bean="serviceLocator"
    factory-method="createClientServiceInstance"/>
```

### import其它配置文件
```xml
<beans>
    <import resource="services.xml"/>
    <import resource="resources/messageSource.xml"/>
    <import resource="/resources/themeSource.xml"/>

    <bean id="bean1" class="..."/>
    <bean id="bean2" class="..."/>
</beans>
```
resource有以下配置规则：
- 根目录和当前目录都是当前文件的所在目录（所以首个/无意义，建议不用）。
- 可以使用 `../` 引用到父级目录，但不推荐
- 可以使用绝对路径，如：`file:C:/config/services.xml` 或 `classpath:/config/services.xml`



### 使用示例
```java
// 创建和配置bean
ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");
// 检索配置的实例
PetStoreService service = context.getBean("petStore", PetStoreService.class);
// 使用配置的实例
List<String> userList = service.getUsernameList();
```


## 基于Java元数据配置
使用 `AnnotationConfigApplicationContext` 去解析这些配置，是ApplicationContext的一个实现。通常需要使用到 @Configuration, @Bean, @Import, 和 @DependsOn 这些注解。

## Web应用中初始化ApplicationContext
不需要采用上述显式代码的方式初始化，可以在 web.xml 中配置一个 `ContextLoaderListener` 来注册 `ApplicationContext`，示例如下：
```xml
<context-param>
	<param-name>contextConfigLocation</param-name>
	<param-value>/WEB-INF/daoContext.xml /WEB-INF/applicationContext.xml</param-value>
</context-param>

<listener>
	<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```
其中 `ContextLoaderListener` 会通过 `contextConfigLocation` 参数（默认值为：`/WEB-INF/applicationContext.xml`）查找xml的元数据配置。
这个配置可以使用Ant-style path（如：/WEB-INF/*Context.xml），多个值也可以用逗号，分号，空格分隔。