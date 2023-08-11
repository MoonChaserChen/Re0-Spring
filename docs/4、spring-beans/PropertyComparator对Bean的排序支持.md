# 对Bean的排序支持
Spring中有个SortDefinition可定义Bean的排序规则：根据Bean的某个属性对Bean进行排序（升降序、忽略大小写），只有一个实现MutableSortDefinition（其toggleAscendingOnProperty有点看不懂，感觉实现和说明不太一致）。
然后可利用 PropertyComparator 对Bean进行排序。下面是一个示例：

## 数据准备
```java
public class PropertyComparatorTest {
    private final List<Pet> pets = new ArrayList<>();
    public static final String PET_NAME_TOM = "Tom";
    public static final String PET_NAME_JERRY = "jerry";

    @Before
    public void before() {
        // 需求：按宠物名升序，忽略大小写
        pets.add(new Pet(1, PET_NAME_TOM, 12));
        pets.add(new Pet(2, PET_NAME_JERRY, 11));
        pets.add(new Pet(3, PET_NAME_TOM, 11));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Pet {
        private int id;
        private String name;
        private int age;
    }
}
```

### 使用PropertyComparator排序
```java
@Test
public void testSort1() {
    // 使用PropertyComparator.sort
    PropertyComparator.sort(pets, new MutableSortDefinition("name", true, true));
    assertEquals(PET_NAME_JERRY, pets.get(0).getName());
}

@Test
public void testSort2() {
    // 使用PropertyComparator对象
    pets.sort(new PropertyComparator<>("name", true, true));
    assertEquals(PET_NAME_JERRY, pets.get(0).getName());
}
```

目前使用JDK8中的lambda也可很好地进行排序了，但是上面这些类是在2003年就定义的，那时还没有lambda表达式。
```java
@Test
public void testSort3() {
    // 不用lambda
    pets.sort(new Comparator<Pet>() {
        @Override
        public int compare(Pet o1, Pet o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    });
    assertEquals(PET_NAME_JERRY, pets.get(0).getName());
}

@Test
public void testSort4() {
    // lambda实现
    pets.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
    assertEquals(PET_NAME_JERRY, pets.get(0).getName());
}
```

### 多重排序
如果将需求改成：按宠物名升序，忽略大小写；其次按年龄升序排序。用了PropertyComparator可以如下实现：
```java
@Test
public void testSort6() {
    // PropertyComparator 实现
    pets.sort(new PropertyComparator<Pet>("name", true, true).thenComparing(Pet::getAge));
    assertEquals(11, pets.get(0).getAge());
}

@Test
public void testSort7() {
    // JDK实现
    Comparator<Pet> petComparator = (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName());
    pets.sort(petComparator.thenComparingInt(Pet::getAge));
    assertEquals(11, pets.get(0).getAge());
}
```

## 实现原理
PropertyComparator 是利用 BeanWrapper 获取到相关字段的值，从而进行比较排序的。
```java
@Test
public void testSort9() {
    BeanWrapperImpl bw = new BeanWrapperImpl();
    bw.setWrappedInstance(new Pet(1, PET_NAME_TOM, 12));
    assertEquals(PET_NAME_TOM, bw.getPropertyValue("name"));
}
```

## 总结
PropertyComparator还可以用，但是直接指定字段名缺少了修改字段名称的约束。如果修改了字段名，代码不会报错（会有DEBUG级别错误信息），但排序就没生效了。