package ink.akira.spring.core;

import ink.akira.spring.Pet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PagedListHolder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        // 查询第2页
        pagedListHolder.setPage(1);
        assertEquals(List.of(11, 12, 13, 14, 15, 16, 17, 18, 19, 20), pagedListHolder.getPageList());
        // 向上越界时自动返回最后1页；向下越界时抛异常（页码数小于0）
        pagedListHolder.setPage(20);
        assertEquals(List.of(91, 92, 93, 94, 95, 96, 97, 98, 99, 100), pagedListHolder.getPageList());

        // 设置页码大小与页码（会重置为第1页）
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

    @Test
    public void testSort() {
        List<Pet> allData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            allData.add(new Pet(i, String.valueOf(i), i));
        }

        // 按Pet::getAge降序排列，目前不会触发，需要调用resort方法
        PagedListHolder<Pet> pagedListHolder = new PagedListHolder<>(allData, new MutableSortDefinition("age", true, false));
        pagedListHolder.setPageSize(5);
        pagedListHolder.resort();
        // 9, 8, 7, 6, 5
        assertEquals(9, pagedListHolder.getPageList().get(0).getAge());
        assertEquals(5, pagedListHolder.getPageList().get(4).getAge());
    }


}
