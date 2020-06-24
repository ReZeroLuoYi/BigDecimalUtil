# BigDecimalUtil
BigDecimalUtil 计算工具类，提供对自然公式的解析、一键赋值、一键格式化等通用方法

- 可以通过计算公式计算结果  
```java
BigDecimal profit = BigDecimalUtil.INSTANCE.computeByFormula("奖励 =（毛利-标准值）*系数" , grossProfit, base, rate);  
```
- 可以通过一个方法将对象的所有BigDecimal的属性设为0  
```java
BigDecimalUtil.setFieldsNullToZero(objectBean);
```
- 可以一键格式化对象中所有BigDecimal的属性
```java
BigDecimalUtil.INSTANCE.formatFields(objectBean);
```
- 累加、累减、累乘、累除，支持各种数据格式
```java
BigDecimal result = BigDecimalUtil.INSTANCE.add(1, 2L, "3", 4.6, new BigDecimal("5"));
```
- and so on
