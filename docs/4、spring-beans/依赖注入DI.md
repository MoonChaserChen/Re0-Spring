# 依赖注入DI
依赖注入（DI）是一个过程，对象仅通过构造参数、工厂方法的参数或在对象实例被构造或从工厂方法返回后在其上设置的属性来定义它们的依赖（即与它们一起工作的其它对象）。
然后，容器在创建 bean 时注入这些依赖。
> 这个过程从根本上说是Bean本身通过使用类的直接构造或服务定位模式来控制其依赖的实例化或位置的逆过程（因此被称为控制反转）。

## DI的优点
- 采用DI原则，代码会更干净，当对象被提供其依赖时，解耦会更有效。对象不会查找其依赖，也不知道依赖的位置或类别。
- 你的类变得更容易测试，特别是当依赖是在接口或抽象基类上时，这允许在单元测试中使用stub或mock实现。

## DI方式
DI有两种方式：基于构造器的依赖注入 和 基于setter的依赖注入。
### 基于构造器的依赖注入
```java
public class ThingOne {
    public ThingOne(ThingTwo thingTwo, ThingThree thingThree) {
        // ...
    }
}
```
```xml
<beans>
    <bean id="beanOne" class="x.y.ThingOne">
        <constructor-arg ref="beanTwo"/>
        <constructor-arg ref="beanThree"/>
    </bean>

    <bean id="beanTwo" class="x.y.ThingTwo"/>

    <bean id="beanThree" class="x.y.ThingThree"/>
</beans>
```
`<constructor-arg>`节点可以用 ref 对一个Bean进行引用，也可以通过value设置具体的值。
但通过 value 设置具体值时，需要指定参数类型或参数index，以便Spring明确构造器参数顺序。
```xml
<bean id="exampleBean" class="examples.ExampleBean">
    <constructor-arg type="int" value="7500000"/>
    <constructor-arg type="java.lang.String" value="42"/>
</bean>

<bean id="exampleBean" class="examples.ExampleBean">
    <constructor-arg index="0" value="7500000"/>
    <constructor-arg index="1" value="42"/>
</bean>
```
其中用index的指定方式还可以减少同参数类型的歧义。
### 基于setter的依赖注入
```xml
<bean id="exampleBean" class="examples.ExampleBean">
    <!-- setter injection using the nested ref element -->
    <property name="beanOne">
        <ref bean="anotherExampleBean"/>
    </property>

    <!-- setter injection using the neater ref attribute -->
    <property name="beanTwo" ref="yetAnotherBean"/>
    <property name="integerProperty" value="1"/>
</bean>

<bean id="anotherExampleBean" class="examples.AnotherBean"/>
<bean id="yetAnotherBean" class="examples.YetAnotherBean"/>
```

在实际创建Bean时，Spring尽可能晚地设置属性和解析依赖关系。

## 自动注入


### 显式指定依赖关系depend-on
“一个Bean被设置为另一个Bean的一个属性”这种依赖关系Spring能感知到，但是有时Bean之间的依赖关系并不那么直接。  
一个例子是当一个类中的静态初始化器需要被触发时，比如数据库驱动程序的注册。这里就需要使用 `depends-on` 来显式指定依赖关系（多个用逗号、空格或分号分隔）。
```xml
<bean id="beanOne" class="ExampleBean" depends-on="manager,accountDao"/>

<bean id="manager" class="ManagerBean" />
<bean id="accountDao" class="x.y.jdbc.JdbcAccountDao" />
```

### 懒加载的Bean
默认情况下，ApplicationContext 的实现会急切地创建和配置所有的 单例 Bean，作为初始化过程的一部分。一般来说，这种预实例化是可取的，因为配置或周围环境中的错误会立即被发现，而不是几小时甚至几天之后。
然而，当懒加载Bean被非懒加载的单例Bean依赖时，ApplicationContext 还是会在启动时创建懒加载 Bean，因为它必须满足单例的依赖关系。
```xml
<bean id="lazy" class="com.something.ExpensiveToCreateBean" lazy-init="true"/>
```

## 循环依赖
Spring如何解决循环依赖
1. 执行构造方法与设置属性分开
2. 设置属性时递归处理

如果使用构造函数注入，就有可能产生一个无法解决的循环依赖情况。
比如说：类A通过构造函数注入需要类B的一个实例，而类B通过构造函数注入需要类A的一个实例。
如果你将A类和B类的Bean配置为相互注入，Spring IoC容器会在运行时检测到这种循环引用，并抛出一个 BeanCurrentlyInCreationException。


## Autowired与Resource
@Autowired注解是按照类型（byType）装配依赖对象，默认情况下它要求依赖对象必须存在，如果允许null值，可以设置它的required属性为false。
如果我们想使用按照名称（byName）来装配，可以结合@Qualifier注解一起使用。
(通过类型匹配找到多个candidate,在没有@Qualifier、@Primary注解的情况下，会使用对象名作为最后的fallback匹配)

@Resource默认按照ByName自动注入，由J2EE提供，需要导入包javax.annotation.Resource。
@Resource有两个重要的属性：name和type，而Spring将@Resource注解的name属性解析为bean的名字，而type属性则解析为bean的类型。
所以，如果使用name属性，则使用byName的自动注入策略，而使用type属性时则使用byType自动注入策略。
如果既不制定name也不制定type属性，这时将通过反射机制使用byName自动注入策略。
1. 如果同时指定了name和type，则从Spring上下文中找到唯一匹配的bean进行装配，找不到则抛出异常。
2. 如果指定了name，则从上下文中查找名称（id）匹配的bean进行装配，找不到则抛出异常。
3. 如果指定了type，则从上下文中找到类似匹配的唯一bean进行装配，找不到或是找到多个，都会抛出异常。
4. 如果既没有指定name，又没有指定type，则自动按照byName方式进行装配；如果没有匹配，则回退为一个原始类型进行匹配，如果匹配则自动装配。