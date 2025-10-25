## Problem

### 注解

@Bean 将方法返回的对象注册为 Spring IoC 容器中的一个 Bean 实例，由 Spring 容器统一管理生命周期

@Configuration 将当前类标识为一个配置类

@ComponentScan 扫描组件

@Override 指示一个方法声明旨在**重写**（Override）父类（超类）中的方法或实现接口中的方法

@EnableWebMvc 用于启用和定制Spring MVC默认配置的模块驱动注解，放在类上

@ControllerAdvice 将当前类标识为异常处理的组件

@ExceptionHandler 用于设置所标识方法处理的异常

## 控制反转（IoC）

#### 组件

在 Spring 里，组件是指被 Spring 容器管理的对象，也就是所谓的 “Bean”。简单来说，只要一个类被 Spring 容器所管理，就可以把它称为组件 。

**常见的组件类型**

`**@Component**`

`**@Service**`用于标识服务层组件，通常在业务逻辑层使用

`**@Repository**`主要用于标识数据访问层（DAO 层）组件，通常在操作数据库相关的类上使用，比如实现数据库的增删改查操作

`**@Controller**`在基于 Spring MVC 的 Web 应用中，用于标识控制器组件，负责接收客户端发送的 HTTP 请求，处理请求并返回响应数据

#### 总结：判断 “是否是组件” 的核心依据

不管是哪种类型，只要满足以下任一条件，就是 Spring 中的组件：

1. 类上有 `@Component`、`@Service`、`@Repository`、`@Controller`、`@Configuration` 等 Spring 注解，且被组件扫描覆盖；
2. 类通过 XML 配置的 `<bean>` 标签定义；
3. 由 `@Configuration` 配置类中的 `@Bean` 方法返回的对象；
4. 实现 Spring 特定接口（如 `ApplicationListener`、`CommandLineRunner`）且被容器管理。

## 依赖注入（DI）

### 

## Bean 生命周期管理

- 创建 Bean 实例
- 注入依赖
- 调用初始化方法
- 使用 Bean
- 调用销毁方法

## 配置管理

支持多种配置方式：

- XML 配置
- 注解配置（@Component, @Service, @Repository 等）
- Java 配置（@Configuration）

## AOP（面向切面编程）支持

## 事务管理