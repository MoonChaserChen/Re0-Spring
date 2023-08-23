# ConfigurableBeanFactory定义的组件
## BeanClassLoader
## TempClassLoader
下面可看到TempClassLoader的设置
```java
public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {
    // 其它代码...
    protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        // 其它代码...
        
        // Detect a LoadTimeWeaver and prepare for weaving, if found.
        if (beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
            beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
            // Set a temporary ClassLoader for type matching.
            beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
        }
        
        // 其它代码...
    }
    // 其它代码...
}
```
## BeanExpressionResolver
下面可看到BeanExpressionResolver的设置
```java
public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {
    // 其它代码...
    protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        // 其它代码...
        
        beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
        
        // 其它代码...
    }
    // 其它代码...
}

public StandardBeanExpressionResolver(@Nullable ClassLoader beanClassLoader) {
    this.expressionParser = new SpelExpressionParser(new SpelParserConfiguration(null, beanClassLoader));
}
```
这里可以看到Spring使用的是自定义的Spel表达式解析器SpelExpressionParser。
## ConversionService
## PropertyEditorRegistrar
## TypeConverter
## StringValueResolver
## BeanPostProcessor
## Scope
## AccessControlContext
这个为什么是get方法？
## BeanDefinition