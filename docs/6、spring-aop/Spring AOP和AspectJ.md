# Spring AOP和AspectJ AOP
Spring AOP是属于运行时增强，而AspectJ是编译时增强。Spring AOP基于代理（Proxying），而AspectJ基于字节码操作（Bytecode Manipulation）。
AspectJ提供了两种切面织入方式，第一种通过特殊编译器，在编译期，将AspectJ语言编写的切面类织入到Java类中，可以通过一个Ant或Maven任务来完成这个操作；
第二种方式是类加载期织入，也简称为LTW（Load Time Weaving）
Spring AOP已经集成了AspectJ，AspectJ应该算得上是Java生态系统中最完整的AOP框架了。AspectJ相比于Spring AOP功能更加强大，但是Spring AOP相对来说更简单。
如果我们的切面比较少，那么两者性能差异不大。但是，当切面太多的话，最好选择AspectJ，它比SpringAOP快很多。

而且SpringAOP的实现中也用到了不少AspectJ的东西（在 `org.springframework.aop.aspectj` 包下）。
比如 `org.springframework.aop.aspectj.AspectJExpressionPointcut`，这是一个使用AspectJ提供的库来解析AspectJ pointcut表达式字符串的pointcut。

| Spring AOP                 | AspectJ AOP                             |
|----------------------------|-----------------------------------------|
| 在纯 Java 中实现                | 使用 Java 编程语言的扩展实现                       |
| 不需要单独的编译过程                 | 除非设置 LTW，否则需要 AspectJ 编译器 (ajc)         |
| 只能使用运行时织入                  | 运行时织入不可用。支持编译时、编译后和加载时织入                |
| 功能不强-仅支持方法级编织              | 更强大 - 可以编织字段、方法、构造函数、静态初始值设定项、最终类/方法等…。 |
| 只能在由 Spring 容器管理的 bean 上实现 | 可以在所有域对象上实现                             |
| 仅支持方法执行切入点                 | 支持所有切入点                                 |
| 代理是由目标对象创建的, 并且切面应用在这些代理上  | 在执行应用程序之前 (在运行时) 前, 各方面直接在代码中进行织入       |
| 比 AspectJ 慢多了              | 更好的性能                                   |
| 易于学习和应用                    | 相对于 Spring AOP 来说更复杂                    |