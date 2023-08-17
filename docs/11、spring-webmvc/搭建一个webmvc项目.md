# 搭建一个webmvc项目
这里介绍快速开始一个Spring WebMVC项目

### 1) 构建一个Maven工程
略
### 2) pom.xml
添加以下依赖
```xml
<dependencies>
    <!--servlet-->
    <dependency>
        <groupId>javax.servlet</groupId>
        <artifactId>javax.servlet-api</artifactId>
        <version>4.0.1</version>
        <scope>provided</scope>
    </dependency>
    <!--spring-->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-webmvc</artifactId>
        <version>5.3.23</version>
    </dependency>
    <!--databind-->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.9.9</version>
    </dependency>
</dependencies>
```
1. javax.servlet-api，Servlet容器（比如Tomcat）一般都会提供此依赖，因此这里的scope填写provided
2. spring-webmvc，spring web项目依赖
3. jackson-databind，添加此依赖后，可针对于Restful API直接返回对象，如[这里](#6-controller)直接返回JsonResult对象：

### 3) web.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">

    <!--spring-->
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:spring/applicationContext.xml</param-value>
    </context-param>

    <!--springMvc-->
    <servlet>
        <servlet-name>dispatcherServlet</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:spring/dispatcher-servlet.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>dispatcherServlet</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
</web-app>
```
1. ContextLoaderListener配置一个Spring容器，并在applicationContext.xml配置会使用到的Beans
2. DispatcherServlet，url-pattern配置为/，表示默认的Servlet入口，根据Servlet规范，如果匹配不到其它Servlet时，将会进入此Servlet。
     这个DispatcherServlet即为Spring对于请求的入口，并进行后续处理（比如分发到各个Controller）。需要注意的是：如果不做其它处理，甚至对于图片等静态文件的请求也会进入到此Servlet。

### 4) applicationContext.xml
配置除Controller外所有的Beans

### 5) dispatcher-servlet.xml
配置Controller相关
```xml
<!--一般Controller上会添加@Controller注解，配合这两个配置，Spring会自动扫描Controller并添加到Spring容器中-->
<mvc:annotation-driven/>
<context:component-scan base-package="ink.akira.springmvcdemo.controller"/>

<!--用于对进入到Spring DispatcherServlet的静态资源进行处理-->
<mvc:default-servlet-handler/>
```

### 6) Controller
```java

package ink.akira.springmvcdemo.pojo;

// 通用返回的数据格式
public class JsonResult {
     public static final int SUCCESS = 0;
     public static final String SUCCESS_MSG = "SUCCESS";
     public static final int ERROR = -1;
     public static final String ERROR_MSG = "ERROR";

     private int status;
     private String message;
     private Object data;

     public JsonResult() {
     }

     public JsonResult(int status, String message, Object data) {
          this.status = status;
          this.message = message;
          this.data = data;
     }

     public int getStatus() {
          return status;
     }

     public void setStatus(int status) {
          this.status = status;
     }

     public String getMessage() {
          return message;
     }

     public void setMessage(String message) {
          this.message = message;
     }

     public Object getData() {
          return data;
     }

     public void setData(Object data) {
          this.data = data;
     }

     public static JsonResult success(Object data) {
          return new JsonResult(SUCCESS, SUCCESS_MSG, data);
     }

     public static JsonResult error(String message) {
          return new JsonResult(ERROR, ERROR_MSG, null);
     }
}


@RestController
public class HelloController {
     @RequestMapping(value="/hello")
     public JsonResult hello(String name) {
          return JsonResult.success("你好, " + name);
     }
}
```
> @RestController表明直接返回Restful的API接口

### 7) 请求
http://localhost:8080/hello?name=a
```json
{
    "status": 0,
    "message": "SUCCESS",
    "data": "你好, a"
}
```

    