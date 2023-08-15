# AliasRegistry
管理别名。

| 方法                                             | 说明      |
|------------------------------------------------|---------|
| void registerAlias(String name, String alias); | 注册别名    |
| void removeAlias(String alias);                | 删除别名    |
| boolean isAlias(String name);                  | 判断是否是别名 |
| String[] getAliases(String name);              | 获取别名    |

## SimpleAliasRegistry
以 `ConcurrentHashMap` alias -> name 实现的别名管理，其内部KV为 alias -> name 。具有以下特点：

### 循环别名检测
如果a的别名是b，b的别名是a。则会出现 `IllegalStateException` 异常。
```java
public class SimpleAliasRegistryTest {
    
    @Test(expected = java.lang.IllegalStateException.class)
    public void testSimple() {
        SimpleAliasRegistry registry = new SimpleAliasRegistry();
        registry.registerAlias("a", "b");
        registry.registerAlias("b", "a");
    }
}
```

### 别名具有传递性
如是a的别名是b，b的别名是c。则a的别名是b和c。
```java
public class SimpleAliasRegistryTest {
    @Test
    public void testInherit() {
        SimpleAliasRegistry registry = new SimpleAliasRegistry();
        registry.registerAlias("a", "b");
        registry.registerAlias("b", "c");
        Assert.assertArrayEquals(new String[]{"b", "c"}, registry.getAliases("a"));
    }
}
```

### 别名覆盖
如果a的别名是c，同时b的别名也是c。那c作为最后的b的别名。
```java
public class SimpleAliasRegistryTest {
    @Test
    public void test2() {
        SimpleAliasRegistry registry = new SimpleAliasRegistry();
        registry.registerAlias("a", "c");
        registry.registerAlias("b", "c");
        Assert.assertArrayEquals(new String[]{}, registry.getAliases("a"));
        Assert.assertArrayEquals(new String[]{"c"}, registry.getAliases("b"));
    }
}
```
特殊：如果a的别名是b，同时b的别名也是b。那b不作为任何的别名（将会从Map中移除）。
```java
public class SimpleAliasRegistryTest {
    @Test
    public void test3() {
        SimpleAliasRegistry registry = new SimpleAliasRegistry();
        registry.registerAlias("a", "b");
        registry.registerAlias("b", "b");
        Assert.assertArrayEquals(new String[]{}, registry.getAliases("a"));
        Assert.assertArrayEquals(new String[]{}, registry.getAliases("b"));
    }
}
```