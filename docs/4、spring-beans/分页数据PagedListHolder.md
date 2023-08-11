# 分页数据PagedListHolder
## 基本使用
```java
public class PagedListHolderTest {
    @Test
    public void testCommon() {
        // 1 ~ 100
        List<Integer> allData = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            allData.add(i + 1);
        }

        PagedListHolder<Integer> pagedListHolder = new PagedListHolder<>(allData);
        // 默认1页10个（pageSize），当前处于第1页（page=0）
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), pagedListHolder.getPageList());
        // 查询第2页(from 0)
        pagedListHolder.setPage(1);
        assertEquals(List.of(11, 12, 13, 14, 15, 16, 17, 18, 19, 20), pagedListHolder.getPageList());
        // 向上越界时自动返回最后1页；向下越界时抛异常（页码数小于0）
        pagedListHolder.setPage(20);
        assertEquals(List.of(91, 92, 93, 94, 95, 96, 97, 98, 99, 100), pagedListHolder.getPageList());

        // 设置页码大小与页码（会重置为第1页page）
        pagedListHolder.setPageSize(5);

        // 页数据
        assertEquals(List.of(1, 2, 3, 4, 5), pagedListHolder.getPageList());
        // 页码
        assertEquals(0, pagedListHolder.getPage());
        // 页大小
        assertEquals(5, pagedListHolder.getPageSize());
        // 总数量
        assertEquals(100, pagedListHolder.getNrOfElements());
        // 页码总数
        assertEquals(20, pagedListHolder.getPageCount());
        // 当前页第一个数据的index
        assertEquals(0, pagedListHolder.getFirstElementOnPage());
        // 当前页最后一个数据的index
        assertEquals(4, pagedListHolder.getLastElementOnPage());
        // 还不太懂下面这三个的意思
        System.out.println(pagedListHolder.getMaxLinkedPages());
        System.out.println(pagedListHolder.getLastLinkedPage());
        System.out.println(pagedListHolder.getFirstLinkedPage());
        // 最后一次setSource的时间或初始化时间
        assertNotNull(pagedListHolder.getRefreshDate());
        // 所有数据
        assertEquals(allData, pagedListHolder.getSource());
        // 排序规则
        assertNotNull(pagedListHolder.getSort());
    }
}
```

## 带排序规则
```java
@Test
public void testSort() {
    List<Pet> allData = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        allData.add(new Pet(i, String.valueOf(i), i));
    }

    // 按Pet::getAge降序排列，目前不会触发，需要调用resort方法
    PagedListHolder<Pet> pagedListHolder = new PagedListHolder<>(allData, new MutableSortDefinition("age", true, false));
    pagedListHolder.setPageSize(5);
    // 进行排序操作
    pagedListHolder.resort();
    // 9, 8, 7, 6, 5
    assertEquals(9, pagedListHolder.getPageList().get(0).getAge());
    assertEquals(5, pagedListHolder.getPageList().get(4).getAge());
}
```

## 在Web应用中使用
需求：展示用户的宠物列表，可按名称、年龄升降序排列；可指定页大小。用户的宠物数一般不会超过30。
1. 定义PageVO，其字段名及格式一般需要和前端沟通。并新增一个通过PagedListHolder来创建的方法
    ```java
    @Data
    public static class PageVO<E> {
        private int page;
        private int pageSize;
        private int total;
        private List<E> data;
    
        // 这个PageVO可以依赖于PagedListHolder，因为SpringWeb项目都有PagedListHolder这个基类
        public static <E> PageVO<E> of(PagedListHolder<E> pagedListHolder) {
            PageVO<E> result = new PageVO<>();
            // PagedListHolder的page从0开始，但是前端的页码一般从1开始，这里做个转换
            result.setPage(pagedListHolder.getPage() + 1);
            result.setPageSize(pagedListHolder.getPageSize());
            result.setTotal(pagedListHolder.getNrOfElements());
            result.setData(pagedListHolder.getPageList());
            return result;
        }
    }
    ```
2. 定义返回的宠物列表数据格式，跟接口相关
    ```java
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class PetVO {
        private int id;
        private String name;
        private int age;
    }
    ```
3. 伪造数据，正式情况一般是通过查询DB获取。
    ```java
    private List<PetVO> getStubPets() {
        List<PetVO> pets = new ArrayList<>();
        int i = 0;
        pets.add(new PetVO(i++, "Aupu", 12));
        pets.add(new PetVO(i++, "dghih", 45));
        pets.add(new PetVO(i++, "gi23wb", 2));
        pets.add(new PetVO(i++, "siahf", 8));
        pets.add(new PetVO(i++, "ivwbi", 34));
        pets.add(new PetVO(i++, "vbwi", 26));
        pets.add(new PetVO(i++, "qskfhi", 12));
        pets.add(new PetVO(i++, "ihgwiu", 15));
        pets.add(new PetVO(i++, "vniwe", 34));
        pets.add(new PetVO(i++, "wiu", 9));
        pets.add(new PetVO(i++, "gwi", 4));
        pets.add(new PetVO(i, "abivw", 5));
        return pets;
    }
    ```
4. 完成请求接口
    ```java
    @RestController
    public class PetController {
    
        @RequestMapping("/getMyPets")
        public PageVO<PetVO> getMyPets(String sortBy, boolean ascending, int pageSize, int currentPage) {
            PagedListHolder<PetVO> pagedListHolder = new PagedListHolder<>();
            pagedListHolder.setSource(getStubPets());
            pagedListHolder.setPageSize(pageSize);
            pagedListHolder.setSort(new MutableSortDefinition(sortBy, true, ascending));
            pagedListHolder.resort();
            // PagedListHolder的page从0开始，但是前端的页码一般从1开始，这里做个转换
            pagedListHolder.setPage(currentPage - 1);
            return PageVO.of(pagedListHolder);
        }
    }
    ```
5. 请求 http://localhost:8080/getMyPets?sortBy=name&pageSize=7&currentPage=2&ascending=true
   ```json
   {
       "page": 2,
       "pageSize": 7,
       "total": 12,
       "data":
       [
           {
               "id": 6,
               "name": "qskfhi",
               "age": 12
           },
           {
               "id": 3,
               "name": "siahf",
               "age": 8
           },
           {
               "id": 5,
               "name": "vbwi",
               "age": 26
           },
           {
               "id": 8,
               "name": "vniwe",
               "age": 34
           },
           {
               "id": 9,
               "name": "wiu",
               "age": 9
           }
       ]
   }
   ```

## 总结
适用于做内存分页，减少了排序、从List中提取页面数据等操作。