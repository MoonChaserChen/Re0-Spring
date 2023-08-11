package ink.akira.spring.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void testSort1() {
        PropertyComparator.sort(pets, new MutableSortDefinition("name", true, true));
        assertEquals(PET_NAME_JERRY, pets.get(0).getName());
    }

    @Test
    public void testSort2() {
        pets.sort(new PropertyComparator<>("name", true, true));
        assertEquals(PET_NAME_JERRY, pets.get(0).getName());
    }

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


    @Test
    public void testSort6() {
        // 按宠物名升序，忽略大小写；其次按年龄升序排序。
        pets.sort(new PropertyComparator<Pet>("name", true, true).thenComparing(Pet::getAge));
        assertEquals(11, pets.get(0).getAge());
    }

    @Test
    public void testSort7() {
        // 按宠物名升序，忽略大小写；其次按年龄升序排序。
        Comparator<Pet> petComparator = (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName());
        pets.sort(petComparator.thenComparingInt(Pet::getAge));
        assertEquals(11, pets.get(0).getAge());
    }

    @Test
    public void testSort8() {
        pets.sort(new PropertyComparator<Pet>("nameX", true, true).thenComparing(Pet::getAge));
        System.out.println(pets);
    }

    @Test
    public void testSort9() {
        BeanWrapperImpl bw = new BeanWrapperImpl();
        bw.setWrappedInstance(new Pet(1, PET_NAME_TOM, 12));
        assertEquals(PET_NAME_TOM, bw.getPropertyValue("name"));
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
