# Environment
应用的运行环境。具有profiles配置管理(如 dev, test, prod 等)、变量配置管理（如各种 properties 文件中配置的变量）。

## UML
```plantuml
interface PropertyResolver
interface Environment
interface ConfigurableEnvironment
interface ConfigurablePropertyResolver
abstract AbstractEnvironment

PropertyResolver <|-- Environment
PropertyResolver <|-- ConfigurablePropertyResolver
Environment <|-- ConfigurableEnvironment
ConfigurablePropertyResolver <|-- ConfigurableEnvironment
ConfigurableEnvironment <|-- AbstractEnvironment
AbstractEnvironment <|-- StandardEnvironment
```
只有 `StandardEnvironment` 一个实现。