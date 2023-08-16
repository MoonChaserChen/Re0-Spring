# 启用AspectJ支持
大概是觉得AspectJ太好用了，所以SpringAOP也可以启用AspectJ来配置AOP。

## 启用AspectJ
首先需要引入AspectJ的依赖：
```xml
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>${aspectj-weaver.version}</version>
</dependency>
```
Spring可利用XML配置 `<aop:aspectj-autoproxy/>` 或注解 `@EnableAspectJAutoProxy` 启动AspectJ支持。


## Pointcut配置总结
1. @bean(“beanId”) -------> bean：交给spring容器管理的对象，粒度：粗粒度 按bean匹配 当前bean中的方法都会执行通知
2. @within(“包名.类名”) ------->粒度：粗粒度 可以匹配多个类
3. @execution("返回值类型 包名.类名.方法名(参数列表）-------> 细粒度：方法参数级别
4. @annotation(“包名.类名”） ------->细粒度：按照注解匹配

| 配置                               | 处理类                       | 说明                                              |
|----------------------------------|---------------------------|-------------------------------------------------|
| bean(“beanId”)                   | AspectJExpressionPointcut | 交给spring容器管理的对象，粒度：粗粒度 按bean匹配 当前bean中的方法都会执行通知 |
| within(“包名.类名”)                  | AspectJExpressionPointcut | 粒度：粗粒度 可以匹配多个类                                  |
| execution("返回值类型 包名.类名.方法名(参数列表) | AspectJExpressionPointcut | 细粒度：方法参数级别                                      |
| @annotation(“包名.类名”)             | AspectJExpressionPointcut | 细粒度：方法参数级别                                      |

## AspectJ配置Pointcut
其实就是用AspectJ中的 `AspectJExpressionPointcut` 来配置Pointcut，包括以下形式：
1. execution: 用于匹配方法执行的连接点。这是在使用Spring AOP时要使用的主要切点指定器。
2. within: 将匹配限制在某些类型内的连接点（使用Spring AOP时，执行在匹配类型内声明的方法）。
3. this: 将匹配限制在连接点（使用Spring AOP时方法的执行），其中bean引用（Spring AOP代理）是给定类型的实例。
4. target: 将匹配限制在连接点（使用Spring AOP时方法的执行），其中目标对象（被代理的应用程序对象）是给定类型的实例。
5. args: 将匹配限制在连接点（使用Spring AOP时方法的执行），其中参数是给定类型的实例。
6. @target: 限制匹配到连接点（使用Spring AOP时方法的执行），其中执行对象的类有一个给定类型的注解。
7. @args: 将匹配限制在连接点（使用Spring AOP时方法的执行），其中实际传递的参数的运行时类型有给定类型的注解。
8. @within: 将匹配限制在具有给定注解的类型中的连接点（使用Spring AOP时，执行在具有给定注解的类型中声明的方法）。
9. @annotation: 将匹配限制在连接点的主体（Spring AOP中正在运行的方法）具有给定注解的连接点上。
10. 在Spring AOP中支持 bean PCD，而在原生的AspectJ织入中不支持。它是AspectJ定义的标准PCD的Spring特定扩展，因此不适用于 @Aspect 模型中声明的切面。

## 组合切点（Pointcut）表达式
你可以通过使用 &&、|| 和 ! 来组合 pointcut 表达式。你也可以通过名称来引用pointcut表达式。
```java
package com.xyz;

@Aspect
public class Pointcuts {

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {}

    @Pointcut("within(com.xyz.trading..*)")
    public void inTrading() {}

    @Pointcut("publicMethod() && inTrading()")
    public void tradingOperation() {}
}
```

### execution
```
execution(modifiers-pattern?
            ret-type-pattern
            declaring-type-pattern?name-pattern(param-pattern)
            throws-pattern?)
```
除了返回类型模式（前面片段中的 ret-type-pattern）、名称模式和参数模式之外的所有部分都是可选的。
返回类型模式决定了方法的返回类型必须是什么，以使连接点被匹配。* 是最常被用作返回类型模式的。
它匹配任何返回类型。全路径的类型名称只在方法返回给定类型时匹配。名称模式匹配方法名称。
你可以使用 * 通配符作为名称模式的全部或部分。如果你指定了一个声明类型模式，包括一个尾部的 . 来连接它和名称模式组件。
参数模式稍微复杂一些：() 匹配一个不需要参数的方法，而 (..) 匹配任何数量（零或更多）的参数。 (*) 模式匹配一个需要任何类型的参数的方法。
(*,String) 匹配一个需要两个参数的方法。第一个参数可以是任何类型，而第二个参数必须是一个 String。

参考文档： https://eclipse.dev/aspectj/doc/released/progguide/semantics-pointcuts.html