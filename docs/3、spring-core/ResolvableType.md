# ResolvableType
对 Java Type 的封装（Class也是Java Type的一种）。

## 使用示例
```java
public class ResolvableTypeTest {
    private HashMap<Integer, List<String>> myMap;

    @Test
    public void testExample() throws NoSuchFieldException {
        ResolvableType t = ResolvableType.forField(getClass().getDeclaredField("myMap"));
        t.getSuperType(); // AbstractMap<Integer, List<String>>
        t.asMap(); // Map<Integer, List<String>>
        t.getGeneric(0).resolve(); // Integer
        t.getGeneric(1).resolve(); // List
        t.getGeneric(1); // List<String>
        t.resolveGeneric(1, 0); // String
    }
}
```