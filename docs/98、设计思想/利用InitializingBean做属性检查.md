# 利用InitializingBean做属性检查
````java
public class DataSourceTransactionManager extends AbstractPlatformTransactionManager
		implements ResourceTransactionManager, InitializingBean {

    private DataSource dataSource;

    @Override
    public void afterPropertiesSet() {
        if (getDataSource() == null) {
            throw new IllegalArgumentException("Property 'dataSource' is required");
        }
    }
    // ------------------
    // other code ignored
    // ------------------
}
````