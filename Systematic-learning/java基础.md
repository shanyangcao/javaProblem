## Problem

#### ThreadLocal的特点

#### ![img](https://cdn.nlark.com/yuque/0/2025/png/56763514/1761181092311-5b325c6a-ebd6-47ce-8c7c-05400714bd3e.png)length什么时候加（）

数组用 `length`（无括号，属性）

字符串用 `length()`（有括号，方法）

集合用 `size()`（有括号，方法）

#### 为什么工具类的构造方法都是私有化

核心目的是**禁止外部创建该类的实例**，确保工具类只能通过静态方法调用。若允许实例化（构造方法公开），会导致无意义的对象创建，浪费内存资源。

#### 不要在循环里定义变量

每次循环，这个变量会变初始值，对下一次计算无效

#### 学了整数最小值的特殊性，为什么会有数没有相对应的相反数

比如一个数整数范围是0~7，那么附属就是-1~-8，从1~7都有对应的负数，而-8没有相反数

#### 巩固左移右移知识

\>> ，>>> ，<< ，<<<对于正数来说都输左移或右移，然后用0补。而对于负数来说>>，<<用1补，>>>，<<<用0补。

且对于非负数来说>>i位等同于除以2的i次方，<<i位等同于乘以2的i次方

![img](https://cdn.nlark.com/yuque/0/2025/png/56763514/1760062424200-16da3d8b-8c0f-4c7a-8d7d-0a9f294973bf.png)这个方法可以用来输出数的二进制形式

![img](https://cdn.nlark.com/yuque/0/2025/png/56763514/1760062497772-62ddae82-6ea9-43d1-af1c-b2ea58cab720.png)为什么不能写成(num & (1<<i) == 1 ? "1" : "0"呢

比如0010左移1位为0100有值，应该输出1，而如果写成那种，与1不相等，则输出0，不符

#### 序列化的作用是什么？

**1. 序列化的核心定义**

序列化（Serialization）是指：**将内存中的对象（如** `**Employee**` **实例）转换为可传输、可存储的格式（如 JSON 字符串、字节数组）的过程**。对应的反序列化（Deserialization）是其逆过程：将传输 / 存储的格式（如 JSON 字符串）转换回内存中的对象。

核心作用：解决 “对象如何在不同场景（进程间、网络传输、持久化）中共享” 的问题 —— 内存中的对象无法直接传输或存储，必须通过序列化转换为统一格式。

**2. 序列化的 3 个常见场景**

**场景 1：网络传输（前后端交互）**

Spring Boot 项目中，后端接口返回 `Employee` 对象时，会自动将对象**序列化为 JSON 字符串**，再通过 HTTP 响应发送给前端：

```
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {

    @GetMapping("/employees/{id}")
    public Employee getEmployee(@PathVariable Integer id) {
        // 用建造者模式创建 Employee 对象
        Employee employee = Employee.builder()
                .id(id)
                .status("active")
                .name("张三")
                .build();
        
        // Spring Boot 自动将 employee 对象序列化为 JSON 字符串返回
        return employee;
    }
}
```

前端收到的响应体（JSON 格式）：

```
{
  "id": 1,
  "status": "active",
  "name": "张三"
}
```

前端再通过 JavaScript 将 JSON 字符串**反序列化为对象**，就能直接使用：

```
// 前端代码（Vue/React 等）
fetch("/employees/1")
  .then(res => res.json()) // 反序列化：JSON 字符串 → JavaScript 对象
  .then(employee => {
    console.log(employee.name); // 输出：张三
    console.log(employee.status); // 输出：active
  });
```

场景 2：对象持久化（存储到文件 / 数据库）

如果需要将 `Employee` 对象长期存储（比如存到本地文件），可以先将其序列化为字节数组或 JSON 字符串，再写入文件：

```
import com.fasterxml.jackson.databind.ObjectMapper; // Jackson 库，Spring Boot 默认集成
import java.io.FileWriter;

public class SerializationDemo {
    public static void main(String[] args) throws Exception {
        // 1. 创建 Employee 对象
        Employee employee = Employee.builder()
                .id(2)
                .status("inactive")
                .name("李四")
                .build();
        
        // 2. 序列化：对象 → JSON 字符串（用 Jackson 的 ObjectMapper）
        ObjectMapper objectMapper = new ObjectMapper();
        String employeeJson = objectMapper.writeValueAsString(employee);
        
        // 3. 将 JSON 字符串写入文件（持久化）
        try (FileWriter writer = new FileWriter("employee.json")) {
            writer.write(employeeJson);
        }
        
        System.out.println("序列化后的 JSON：" + employeeJson);
        // 输出：{"id":2,"status":"inactive","name":"李四"}
    }
}
```

后续需要使用该对象时，再从文件读取 JSON 字符串，反序列化为 `Employee` 实例：

```
// 反序列化：JSON 字符串 → 对象
Employee employeeFromFile = objectMapper.readValue(new File("employee.json"), Employee.class);
System.out.println(employeeFromFile.getName()); // 输出：李四
```

场景 3：跨进程通信（如 RPC 调用）

在分布式系统中，不同服务（进程）之间调用时（比如 A 服务调用 B 服务的方法），需要将参数对象（如 `Employee`）序列化为字节流，通过网络传输到 B 服务，B 服务再反序列化为对象进行处理。

## 反射（Reflection）

- **核心作用**：在运行时获取类的元数据（如类名、方法、字段、注解等），并动态调用类的方法、访问或修改字段值，无需在编译期知道具体的类信息。
- **常见应用**：


- - 框架底层（如 Spring 的依赖注入、MyBatis 的 Mapper 接口实现）。
  - 序列化 / 反序列化（如 Jackson、Gson 解析 JSON 为对象）。
  - 通用工具类（如通过反射统一处理不同类的属性）。

## 动态代理

##### 动态代理java知识

动态代理位于 `java.lang.reflect` 包下，主要涉及两个核心类：

- `**java.lang.reflect.Proxy**`：这是用于**创建**代理对象的工具类。它的核心静态方法是 `newProxyInstance()`。
- `**java.lang.reflect.InvocationHandler**`：这是一个**接口**。你需要实现它来自定义代理逻辑。代理对象的所有方法调用都会被转发到 `InvocationHandler` 的 `invoke()` 方法中。
- newProxyInstance参数是`ClassLoader loader`，`Class<?>[] interfaces`，`InvocationHandler h`分别指定代理类的类加载器，用于指定代理类需要实现的接口，实现该接口的类用于定义代理对象的方法调用逻辑

**它的作用是：** 在程序**运行时**，动态地创建一个实现了指定接口的代理类及其对象。你不需要像静态代理那样，手动为每个类编写一个代理类。

##### 动态代理与 Spring 的关系

虽然动态代理是 Java 的功能，但 **Spring 框架极大地推广并简化了它的使用**

Spring 在两个方面重度依赖动态代理：

a) Spring AOP (面向切面编程)

- **如何工作？**

1. 1. 你定义了一个“切面”（Aspect），例如一个用于事务管理的 `@Transactional` 注解。
   2. 当 Spring 容器启动时，它会发现被 `@Transactional` 标注的 Bean。
   3. Spring 会使用动态代理技术，**将这些 Bean 包装在一个代理对象中**。
   4. 当你的代码调用 `userService.updateUser()` 时，你实际上是在调用代理对象的方法。
   5. 代理对象会先执行切面逻辑（如**开启事务**），然后调用**目标Bean的真实方法**，最后再执行切面逻辑（如**提交或回滚事务**）。

- **两种实现方式：**


- - **JDK 动态代理**：**默认方式**。如果目标类**实现了接口**，Spring 就会使用 JDK 动态代理来创建代理对象。代理对象会实现相同的接口。
  - **CGLIB 动态代理**：如果目标类**没有实现任何接口**，Spring 会使用 CGLIB 库。CGLIB 通过**生成目标类的子类**来创建代理对象。因此，`final` 类或 `final` 方法无法被代理。

b) 编程式事务管理

Spring 的 `TransactionTemplate` 等编程式事务管理工具，其底层也是通过动态代理和线程绑定的机制来实现的

##### 动态代理与MyBatis的关系

在 MyBatis 中，Mapper 接口没有实现类，但却能通过 SqlSession 获取到对应的实现对象，这背后也是利用了动态代理技术。MyBatis 通过动态代理生成 Mapper 接口的代理对象，在调用代理对象的方法时，会根据方法签名等信息，匹配到对应的 SQL 语句并执行 。

## 字节码操作

- **核心作用**：在运行时直接修改或生成 `.class` 字节码文件，动态创建新类或修改已有类的结构（如添加方法、字段、注解）。
- **常用库**：


- - **Javassist**：API 简单，适合快速实现字节码修改（如动态添加方法逻辑）。
  - **ASM**：更底层，性能好，但使用较复杂，适合需要精细控制字节码的场景（如 Android 插件化）。
  - **CGLIB**：基于 ASM 封装，常用于生成子类代理（见动态代理）。


- **常见应用**：


- - 框架中的类增强（如 Spring 对类的动态代理）。
  - 代码生成工具（如根据数据库表结构动态生成实体类）。
  - AOP 框架的字节码织入（如 AspectJ 的编译期 / 运行期织入）

### `Properties`类

`Properties`类最常见的用途是读取`.properties`格式的配置文件