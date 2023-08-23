# Spring的命名风格
## get与set的规范
Spring提供了XxxCapable、XxxAware的接口来定义get、set规范，例如：
```java
public interface EnvironmentCapable {
    Environment getEnvironment();
}

public interface EnvironmentAware extends Aware {
	void setEnvironment(Environment environment);
}
```

## 无实际操作的类
无实际操作的类的命名可类似于 `NoOpXxx`，例如： 
* `org.apache.commons.logging.impl.NoOpLog`
* `org.springframework.cglib.proxy.NoOpGenerator`
* `org.springframework.cache.support.NoOpCache`