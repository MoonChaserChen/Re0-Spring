# get与set的规范
Spring提供了XxxCapable、XxxAware的接口来定义get、set规范，例如：
```java
public interface EnvironmentCapable {
    Environment getEnvironment();
}

public interface EnvironmentAware extends Aware {
	void setEnvironment(Environment environment);
}
```