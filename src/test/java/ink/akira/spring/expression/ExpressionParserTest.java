package ink.akira.spring.expression;

import ink.akira.spring.MyUtils;
import ink.akira.spring.Pet;
import ink.akira.spring.Student;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;
import java.util.Map;

public class ExpressionParserTest {
    @Test
    public void testParseExpression() {
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression("'Hello World'.concat('!')");
        Assert.assertEquals("Hello World!", exp.getValue());
    }

    @Test
    public void testParseExpression1() {
        ExpressionParser parser = new SpelExpressionParser();
        System.out.println(parser.parseExpression("T(Math).random() * 100.0").getValue(Double.class)); // 随机 1-100之前的数
        System.out.println(parser.parseExpression("T(ink.akira.spring.MyUtils).concat('a', 'b', 'c')").getValue(String.class)); // abc
    }

    @Test
    public void testParseExpression2() {
        Pet pet = new Pet(1, "Tom", 12);
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression("age + 1900");
        Integer value = exp.getValue(pet, Integer.class); // 1912
    }

    @Test
    public void testParseExpression3() {
        Pet pet = new Pet(1, "Tom", 12);
        EvaluationContext ec = new StandardEvaluationContext(pet);

        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression("age + 1900");
        Integer value = exp.getValue(ec, Integer.class); // 1912
    }

    @Test
    public void testParseExpression4() {
        Pet pet = new Pet(1, "Tom", 12);
        EvaluationContext ec = new StandardEvaluationContext(pet);

        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression("age > 15");
        Boolean value = exp.getValue(ec, Boolean.class); // false
        System.out.println(value);
    }

    @Test
    public void testParseExpression5() {
        Pet pet1 = new Pet(1, "Tom", 12);
        Pet pet2 = new Pet(2, "jerry", 10);
        Student student = new Student();
        student.setName("Akira");
        student.setPets(List.of(pet1, pet2));
        student.setMappedPets(Map.of("Tom", pet1, "jerry", pet2));
        EvaluationContext ec = new StandardEvaluationContext(student);

        ExpressionParser parser = new SpelExpressionParser();
        String value = parser.parseExpression("pets[0].name").getValue(ec, String.class); // Tom
        Integer value1 = parser.parseExpression("mappedPets['Tom'].age").getValue(ec, Integer.class); // 12
        System.out.println(value);
        System.out.println(value1);
    }

    @Test
    public void testParseExpression6() {
        EvaluationContext ec = new StandardEvaluationContext();

        ExpressionParser parser = new SpelExpressionParser();
        System.out.println(parser.parseExpression("5 + 6").getValue(ec, Integer.class)); // 11
        System.out.println(parser.parseExpression("5 gt 6").getValue(ec, Boolean.class)); // false
        System.out.println(parser.parseExpression("5 gt 6 or 1 != 0").getValue(ec, Boolean.class)); // true
        System.out.println(parser.parseExpression("1 - -3").getValue(ec, Integer.class)); // 4
        System.out.println(parser.parseExpression("2 * 3e1 * 4").getValue(ec, Integer.class)); // 240
        System.out.println(parser.parseExpression("1+2-3*8").getValue(ec, Integer.class)); // -21
        System.out.println(parser.parseExpression("1 + 1 == 2 ? 10 : 12").getValue(ec, Integer.class)); // 10

        // Elvis表达式，相当于： name != null ? name :  "UNKNOWN"
        // #name 是变量，由于没有定义，为null
        System.out.println(parser.parseExpression("#name ?: 'UNKNOWN'").getValue(ec, String.class)); // UNKNOWN
    }

    @Test
    public void testParseExpression7() {
        EvaluationContext ec = new StandardEvaluationContext();
        ExpressionParser parser = new SpelExpressionParser();

        System.out.println(parser.parseExpression("{1,2,3,4}").getValue(ec, List.class)); // [1, 2, 3, 4]
        System.out.println(parser.parseExpression("{{1,2,3,4}, {5,6,7,8}}").getValue(ec, List.class)); // [[1, 2, 3, 4], [5, 6, 7, 8]]

        System.out.println(parser.parseExpression("{name:'Nikola',dob:'10-July-1856'}").getValue(ec, Map.class)); // {name=Nikola, dob=10-July-1856}
    }

    @Test
    public void testParseExpression8() {
        EvaluationContext ec = new StandardEvaluationContext();
        ExpressionParser parser = new SpelExpressionParser();

        System.out.println(parser.parseExpression("new ink.akira.spring.Pet(1, 'Tom', 12)").getValue(ec, Pet.class)); // Pet(id=1, name=Tom, age=12)
    }

    @Test
    public void testParseExpression9() {
        Pet tom = new Pet(1, "Tom", 11);
        EvaluationContext ec = new StandardEvaluationContext(tom);
        ec.setVariable("age", 12);
        ExpressionParser parser = new SpelExpressionParser();

        System.out.println(parser.parseExpression("#age > 11").getValue(ec, Boolean.class)); // true
        parser.parseExpression("age = #age").getValue(ec); // 12
        System.out.println(tom.getAge());
    }

    @Test
    public void testParseExpression10() throws NoSuchMethodException {
        EvaluationContext ec = new StandardEvaluationContext();
        ec.setVariable("concat", MyUtils.class.getDeclaredMethod("concat", String[].class));
        ExpressionParser parser = new SpelExpressionParser();

        System.out.println(parser.parseExpression("#concat('a', 'b', 'c')").getValue(ec, String.class)); // true
    }

    @Test
    public void testParseExpression11() {
        Pet pet = new Pet(1, null, 12);
        EvaluationContext ec = new StandardEvaluationContext(pet);
        ExpressionParser parser = new SpelExpressionParser();

        // 由于这里name为null，避免NPE
        System.out.println(parser.parseExpression("name?.length").getValue(ec, Integer.class)); // null
    }

    @Test
    public void testParseExpression12() {
        Pet pet1 = new Pet(1, "Tom", 12);
        Pet pet2 = new Pet(2, "jerry", 10);
        Pet pet3 = new Pet(3, "Marry", 3);
        Student student = new Student();
        student.setName("Akira");
        student.setPets(List.of(pet1, pet2, pet3));
        student.setMappedPets(Map.of("Tom", pet1, "jerry", pet2, "Marry", pet3));
        EvaluationContext ec = new StandardEvaluationContext(student);

        ExpressionParser parser = new SpelExpressionParser();
        System.out.println(parser.parseExpression("pets.?[age > 10]").getValue(ec, List.class)); // [Pet(id=1, name=Tom, age=12)]
        System.out.println(parser.parseExpression("mappedPets.?[value.age < 10]").getValue(ec, List.class)); // [{Marry=Pet(id=3, name=Marry, age=3)}]
    }

    @Test
    public void testParseExpression13() {
        Pet pet1 = new Pet(1, "Tom", 12);
        Pet pet2 = new Pet(2, "jerry", 10);
        Pet pet3 = new Pet(3, "Marry", 3);
        Student student = new Student();
        student.setName("Akira");
        student.setPets(List.of(pet1, pet2, pet3));
        EvaluationContext ec = new StandardEvaluationContext(student);

        ExpressionParser parser = new SpelExpressionParser();
        System.out.println(parser.parseExpression("pets.![name]").getValue(ec, List.class)); // [Tom, jerry, Marry]
    }


}
