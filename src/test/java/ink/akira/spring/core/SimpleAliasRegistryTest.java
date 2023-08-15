package ink.akira.spring.core;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.SimpleAliasRegistry;


public class SimpleAliasRegistryTest {

    @Test(expected = java.lang.IllegalStateException.class)
    public void testSimple() {
        SimpleAliasRegistry registry = new SimpleAliasRegistry();
        registry.registerAlias("a", "b");
        registry.registerAlias("b", "a");
    }

    @Test
    public void testInherit() {
        SimpleAliasRegistry registry = new SimpleAliasRegistry();
        registry.registerAlias("a", "b");
        registry.registerAlias("b", "c");
        Assert.assertArrayEquals(new String[]{"b", "c"}, registry.getAliases("a"));
    }

    @Test
    public void test2() {
        SimpleAliasRegistry registry = new SimpleAliasRegistry();
        registry.registerAlias("a", "c");
        registry.registerAlias("b", "c");
        Assert.assertArrayEquals(new String[]{}, registry.getAliases("a"));
        Assert.assertArrayEquals(new String[]{"c"}, registry.getAliases("b"));
    }

    @Test
    public void test3() {
        SimpleAliasRegistry registry = new SimpleAliasRegistry();
        registry.registerAlias("a", "b");
        registry.registerAlias("b", "b");
        Assert.assertArrayEquals(new String[]{}, registry.getAliases("a"));
        Assert.assertArrayEquals(new String[]{}, registry.getAliases("b"));
    }
}
