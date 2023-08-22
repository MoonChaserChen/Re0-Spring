# AnnotationDrivenBeanDefinitionParser

## `<tx:annotation-driven/>`原理
以JDBC事务为例，通过AOP形成需要以下几个部分：

| 组成                 | 说明                               | 创建方                              |
|--------------------|----------------------------------|----------------------------------|
| DataSource         | 跟JDBC数据库相关                       | 自行创建                             |
| TransactionManager | 通常为DataSourceTransactionManager  | 自行创建。Spring通过名称或类型自动引用。          |
| Target             | 需要添加事务的部分代码                      | 业务代码，自行创建                        |
| Advice             | AOP事务增强                          | Spring创建，为TransactionInterceptor |
| pointcut           | 需要在哪里添加事务                        | 由@Controller指定                   |
| advisor            | pointcut + Advice。事务和pointcut的绑定 | 由@Controller指定                   |

通过上面的表格可知，需要Spring做的为两部分：
1. Advice。AOP事务增强，其逻辑固定抛异常则rollback，无异常则commit，实现类为TransactionInterceptor。
2. 事务的开启方式。如何回滚、隔离级别、传播方式、超时时间等
3. pointcut及与Advice的绑定。通过添加@Controller注解让Spring知道并进行绑定。

### 1) TransactionInterceptor创建
在配置了 `<tx:annotation-driven/>` 后，将会由 `org.springframework.transaction.config.AnnotationDrivenBeanDefinitionParser` 对其进行解析，解析的时候就会生成一个 `TransactionInterceptor`。相关代码如下：
```java
class AnnotationDrivenBeanDefinitionParser implements BeanDefinitionParser {
    private static class AopAutoProxyConfigurer {
        public static void configureAutoProxyCreator(Element element, ParserContext parserContext) {

            String txAdvisorBeanName = TransactionManagementConfigUtils.TRANSACTION_ADVISOR_BEAN_NAME;
            if (!parserContext.getRegistry().containsBeanDefinition(txAdvisorBeanName)) {
                // ------------------
                // code simplified
                // ------------------
                
                // 构建 TransactionInterceptor 的BeanDefinition
                RootBeanDefinition interceptorDef = new RootBeanDefinition(TransactionInterceptor.class);
                interceptorDef.setSource(eleSource);
                interceptorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
                // 自动找到TransactionManager
                registerTransactionManager(element, interceptorDef);
                interceptorDef.getPropertyValues().add("transactionAttributeSource", new RuntimeBeanReference(sourceName));
                String interceptorName = parserContext.getReaderContext().registerWithGeneratedName(interceptorDef);

                CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), eleSource);
                compositeDef.addNestedComponent(new BeanComponentDefinition(interceptorDef, interceptorName));
                // 将TransactionInterceptor注册到Spring容器里
                parserContext.registerComponent(compositeDef);
            }
        }
    }
}
```
TransactionInterceptor为Around切面，当原业务方法执行抛异常时，将调用TransactionManager进行rollback，因此上面的 `registerTransactionManager(..)` 方法用以自动注入 `TransactionManager`，其逻辑如下：
```java
class AnnotationDrivenBeanDefinitionParser implements BeanDefinitionParser {
    private static void registerTransactionManager(Element element, BeanDefinition def) {
        def.getPropertyValues().add("transactionManagerBeanName", TxNamespaceHandler.getTransactionManagerName(element));
    }
}

public class TxNamespaceHandler extends NamespaceHandlerSupport {

    // 通过这个属性指定
    static final String TRANSACTION_MANAGER_ATTRIBUTE = "transaction-manager";

    // 未指定时默认的transactionManager名称
    static final String DEFAULT_TRANSACTION_MANAGER_BEAN_NAME = "transactionManager";


    static String getTransactionManagerName(Element element) {
        return (element.hasAttribute(TRANSACTION_MANAGER_ATTRIBUTE) ?
                element.getAttribute(TRANSACTION_MANAGER_ATTRIBUTE) : DEFAULT_TRANSACTION_MANAGER_BEAN_NAME);
    }
}
```
也就是可主动指定TransactionManager的BeanName，在不主要指定的时候默认为：transactionManager。可采用如下方式主动指定：
```xml
<tx:annotation-driven transaction-manager="txManager"/>
```
### 2) 事务的开启方式
事务的开启方式都是在 `@Transactional` 上配置的，然后通过 `SpringTransactionAnnotationParser` 转换成 `TransactionAttribute`。
```java
public @interface Transactional {
	@AliasFor("transactionManager")
	String value() default "";
    
	@AliasFor("value")
	String transactionManager() default "";

	String[] label() default {};

	Propagation propagation() default Propagation.REQUIRED;
    
	Isolation isolation() default Isolation.DEFAULT;
    
	int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;
    
	String timeoutString() default "";
    
	boolean readOnly() default false;
    
	Class<? extends Throwable>[] rollbackFor() default {};
    
	String[] rollbackForClassName() default {};
    
	Class<? extends Throwable>[] noRollbackFor() default {};
    
	String[] noRollbackForClassName() default {};
}
```
### 3) pointcut及与Advice的绑定
AnnotationTransactionAttributeSource will directly convert this @Transactional 's attributes to properties in RuleBasedTransactionAttribute.


## 草稿
Parses the `<tx:annotation-driven/>` tag. Will register an AutoProxyCreator with the container as necessary.

InfrastructureAdvisorAutoProxyCreator ->


registerComponent
org.springframework.aop.config.AopConfigUtils.AUTO_PROXY_CREATOR_BEAN_NAME =
"org.springframework.aop.config.internalAutoProxyCreator";

org.springframework.transaction.config.internalTransactionAdvisor -->

XmlReaderContext
org.springframework.transaction.annotation.AnnotationTransactionAttributeSource#0 --> org.springframework.transaction.annotation.AnnotationTransactionAttributeSource
org.springframework.transaction.interceptor.TransactionInterceptor#0 --> class org.springframework.transaction.interceptor.TransactionInterceptor

org.springframework.transaction.config.internalTransactionAdvisor --> BeanFactoryTransactionAttributeSourceAdvisor


### CompositeComponentDefinition
org.springframework.transaction.annotation.AnnotationTransactionAttributeSource --> org.springframework.transaction.annotation.AnnotationTransactionAttributeSource#0
class org.springframework.transaction.interceptor.TransactionInterceptor --> org.springframework.transaction.interceptor.TransactionInterceptor#0
org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor --> org.springframework.transaction.config.internalTransactionAdvisor


BeanFactoryTransactionAttributeSourceAdvisor

## spring.handlers
在 `spring-tx` 模块的 `META-INF/spring.handlers` 文件内容是：
```
http\://www.springframework.org/schema/tx=org.springframework.transaction.config.TxNamespaceHandler
```
这个文件有什么作用呢？会直接创建这个类并调用init方法吗？
```java
public class TxNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("advice", new TxAdviceBeanDefinitionParser());
		registerBeanDefinitionParser("annotation-driven", new AnnotationDrivenBeanDefinitionParser());
		registerBeanDefinitionParser("jta-transaction-manager", new JtaTransactionManagerBeanDefinitionParser());
	}
    // ------------------
    // other code ignored
    // ------------------
}
```
