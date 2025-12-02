## Problem

#### 什么时候需要在方法上写@Bean

在 Spring 框架中，`@Bean` 注解用于**告诉 Spring 容器：这个方法会返回一个对象，该对象需要被 Spring 管理（纳入 IoC 容器）**。简单来说，当你需要手动定义一个 “由 Spring 管控的对象” 时，就需要在方法上使用 `@Bean`。

**具体场景：什么时候必须用** `**@Bean**`**？**

**1.** **第三方组件的实例化**

当你需要使用**非自定义的类**（如第三方库中的类，如 `DruidDataSource`、`RestTemplate`、`ThreadPoolExecutor` 等），且这些类无法通过 `@Component` 及其衍生注解（`@Service`、`@Controller` 等）标注（因为你不能修改第三方类的源码），此时必须通过 `@Bean` 方法手动创建其实例，并交给 Spring 管理。

**示例**：配置 Druid 数据源（第三方类，无法加 `@Component`）：

```
@Configuration // 标记这是一个配置类
public class DataSourceConfig {
    // 用 @Bean 告诉 Spring：这个方法返回的对象需要被容器管理
    @Bean
    public DruidDataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/test");
        dataSource.setUsername("root");
        // ... 其他配置
        return dataSource; // 返回的对象会被 Spring 纳入 IoC 容器
    }
}
```

**2. 需要自定义实例化逻辑的对象**

即使是自定义类，若其**实例化过程复杂**（如需要动态参数、条件判断、依赖其他资源等），`@Component` 注解（默认通过无参构造器实例化）无法满足需求，此时需要用 `@Bean` 方法手动控制实例化过程。

**示例**：根据环境变量动态创建对象：

```
@Configuration
public class MyConfig {
    @Bean
    public MyService myService() {
        MyService service = new MyService();
        // 根据环境变量设置不同的属性
        if ("prod".equals(System.getenv("ENV"))) {
            service.setMode("production");
        } else {
            service.setMode("development");
        }
        return service;
    }
}
```

**3. 整合框架或组件时的配置类**

在整合 Spring 与其他框架（如 MyBatis、Redis、Quartz 等）时，需要创建框架所需的核心对象（如 `SqlSessionFactory`、`RedisTemplate`），这些对象的创建通常需要依赖其他 Spring 管理的对象（如数据源），必须通过 `@Bean` 方法定义。

**示例**：配置 MyBatis 的 `SqlSessionFactory`：

```
@Configuration
public class MyBatisConfig {
    // 依赖 Spring 容器中的 dataSource（通过 @Bean 注入）
    @Bean
    public SqlSessionFactory sqlSessionFactory(DruidDataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource); // 关联数据源
        // ... 其他配置（如 mapper 路径）
        return sessionFactory.getObject();
    }
}
```

**4. 多实例对象的管理**

`@Component` 标注的类默认是单例（Spring 容器中只有一个实例），若需要**多实例对象**（每次获取时创建新实例），可通过 `@Bean` 配合 `@Scope("prototype")` 实现。

**示例**：定义多实例对象：

```
@Configuration
public class PrototypeConfig {
    @Bean
    @Scope("prototype") // 每次获取时创建新实例
    public User user() {
        return new User();
    }
}
```

`**@Bean**` **的使用规则**

1. **必须搭配** `**@Configuration**` **或** `**@Component**`：`@Bean` 方法必须定义在被 `@Configuration`（推荐）或 `@Component` 标注的类中，否则 Spring 无法扫描到该方法。
2. **方法名默认作为 Bean 的名称**：如 `@Bean public User user()`，默认 Bean 名称为 `user`，也可通过 `@Bean("customName")` 自定义名称。
3. **依赖自动注入**：`@Bean` 方法的参数会自动从 Spring 容器中获取（如上述 `sqlSessionFactory` 方法中的 `dataSource` 参数），无需额外标注 `@Autowired`。

#### Spring 管控的对象有什么作用

Spring 管控的对象（即由 Spring IoC 容器管理的 Bean）是 Spring 框架的核心，其作用贯穿整个应用的生命周期，核心价值在于**简化开发、解耦组件、提升系统可维护性和扩展性**。具体作用可从以下几个方面展开：

**1.** **自动实例化与生命周期管理**

Spring 容器会**自动负责对象的创建、初始化和销毁**，开发者无需手动通过 `new` 关键字创建对象，也无需关心对象何时销毁，极大简化了代码。

- **实例化**：容器根据配置（如 `@Component`、`@Bean`）自动创建对象，避免了硬编码的 `new UserService()` 等操作，减少重复代码。
- **生命周期管理**：通过 `@PostConstruct`（初始化）、`@PreDestroy`（销毁）等注解，或实现 `InitializingBean`、`DisposableBean` 接口，可在对象创建后、销毁前执行自定义逻辑（如数据库连接初始化、资源释放）。
- **单例默认策略**：Spring 管理的对象默认是单例（整个容器中只有一个实例），减少了对象频繁创建销毁的性能开销，同时保证了全局状态的一致性（如工具类、配置类）。

**2.** **依赖注入（DI），解决组件耦合**

这是 Spring 管控对象最核心的作用之一。在传统开发中，对象之间的依赖关系需要手动维护（如 `A a = new A(); B b = new B(a);`），导致代码耦合严重，修改一个类可能影响多个依赖它的类。而 Spring 管控的对象通过**依赖注入**自动建立依赖关系：

- 容器会分析对象之间的依赖（如 `Service` 依赖 `Repository`，`Controller` 依赖 `Service`），并在创建对象时自动将依赖的 Bean 注入到目标对象中（通过 `@Autowired`、构造器注入等方式）。
- 示例：**java**运行

```
@Service
public class UserService {
    // 依赖的 UserRepository 由 Spring 自动注入，无需手动 new
    @Autowired
    private UserRepository userRepository;
}
```

这种方式彻底消除了硬编码的依赖关系，实现了 “组件面向接口编程，依赖由容器动态注入”，极大降低了代码耦合度。

**3.** **统一的配置与管理**

Spring 管控的对象可以通过**外部配置**（如 `application.properties`、`@Value` 注解）动态设置属性，无需修改代码即可调整对象行为，提升了系统的灵活性。

- 例如，数据源（`DataSource`）作为 Spring 管理的 Bean，其 URL、用户名、密码等信息可配置在外部文件中，切换环境（开发 / 测试 / 生产）时只需修改配置，无需改动代码：**java**运行

```
@Bean
public DruidDataSource dataSource(
        @Value("${spring.datasource.url}") String url,
        @Value("${spring.datasource.username}") String username) {
    DruidDataSource ds = new DruidDataSource();
    ds.setUrl(url);
    ds.setUsername(username);
    return ds;
}
```

#### 是不是可以认为springboot项目，所有返回对象的方法都要加上@Bean注解

当然**不是**。`@Bean` 注解的使用有明确的场景限制，并非 Spring Boot 项目中所有返回对象的方法都需要加 `@Bean`。如果滥用 `@Bean`，反而会导致 Spring 容器管理的对象冗余，甚至引发逻辑混乱。

**关键区别：“需要 Spring 管控的对象” vs “普通对象”**

`@Bean` 的核心作用是**将对象交给 Spring IoC 容器管理**（即成为 “Bean”）。但在实际开发中，很多对象并不需要 Spring 管控，比如：

- 业务逻辑中临时创建的对象（如 `User`、`Order` 等数据模型）；
- 方法内部的局部变量（如循环中创建的临时集合）；
- 不需要被其他组件依赖、也不需要生命周期管理的对象。

这些对象属于 “普通对象”，应该由开发者通过 `new` 手动创建，而不是通过 `@Bean` 交给 Spring 管理。

**哪些情况不需要加** `@Bean`**？**

**1. 普通业务对象（POJO、DTO 等数据模型）**

例如用户实体、订单对象等，它们是业务逻辑中传递的数据载体，不需要 Spring 管控，直接通过 `new` 创建即可：

**java**

运行

```
// 不需要加 @Bean，直接 new 即可
public User createUser(String name) {
    User user = new User();
    user.setName(name);
    return user;
}
```

如果给这类方法加 `@Bean`，会导致 Spring 容器中创建大量无关的 User 实例（甚至可能因单例策略导致数据混乱）。

**2. 方法内部的局部对象**

例如工具类方法中创建的临时对象，仅在方法内部使用，无需 Spring 管理：

**java**

运行

```
public class StringUtils {
    // 局部对象，无需 @Bean
    public static List<String> splitString(String str) {
        List<String> result = new ArrayList<>(); // 仅方法内使用，无需 Spring 管控
        // ... 分割逻辑
        return result;
    }
}
```

**3. 已通过** `@Component` **及其衍生注解管理的对象**

如果一个类已经被 `@Service`、`@Controller`、`@Repository` 等注解标注（这些注解本质是 `@Component` 的特例），Spring 会自动扫描并创建其实例（成为 Bean），无需再通过 `@Bean` 方法重复定义：

**java**

运行

```
@Service // 已被 Spring 管控，无需再用 @Bean 定义
public class UserService {
    // ...
}
```

此时若再写一个 `@Bean` 方法返回 `UserService` 实例，会导致 Spring 容器中出现多个同名 Bean，引发冲突。

**只有 “需要 Spring 管控的对象” 才需要** `@Bean`

**再次强调：**`@Bean` **仅用于手动向 Spring 容器注册需要被管理的对象，典型场景包括：**

- 第三方组件（如 `DruidDataSource`、`RestTemplate`）；
- 复杂实例化逻辑的对象（如需要动态参数、条件判断的对象）；
- 框架整合时的核心组件（如 `SqlSessionFactory`、`RedisTemplate`）。

对于普通业务对象、局部对象、已被 `@Component` 标注的对象，**绝对不需要加** `**@Bean**`。

#### 什么时候在类上面加@Component，@Service、@Controller注解

在 Spring（包括 Spring Boot）中，`@Component`、`@Service`、`@Controller` 都是用于**标识类为 “Spring 管理的 Bean”** 的注解，本质上是为了让 Spring 自动扫描并将这些类的实例纳入 IoC 容器。它们的核心作用一致，但语义不同，分别对应不同的业务场景，使用时需根据类的**职责**选择。

**1.** `**@Component**`**：通用组件注解（最基础）**

`@Component` 是所有 Spring 管理组件的**父注解**，是一个通用注解，没有明确的业务含义，适用于**无法归类到** `**@Service**`**、**`**@Controller**` **等更具体注解的类**。

**适用场景：**

- 工具类（如 `DateUtils`、`FileUtils`）：提供通用功能，不直接属于业务逻辑层、控制层等。
- 自定义组件（如 `MyCacheComponent`）：实现特定功能，但不属于 MVC 分层中的任何一层。
- 中间件集成类（如 `MessageListener`）：处理消息监听等跨层逻辑。

```
// 通用工具类，用 @Component 标识为 Spring 管理的 Bean
@Component
public class DateUtils {
    public String format(Date date) {
        // 日期格式化逻辑
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
```

**2.** `**@Service**`**：业务逻辑层注解**

`@Service` 是 `@Component` 的衍生注解，专门用于**业务逻辑层（Service 层）** 的类，语义上表示 “处理核心业务逻辑”。

**核心职责：**

- 封装业务逻辑（如用户注册、订单处理、数据校验等）。
- 协调多个数据访问层（Repository/DAO）的操作。
- 事务控制通常在 `@Service` 层通过 `@Transactional` 实现。

**适用场景：**

- 所有以 `XXXService` 命名的类（如 `UserService`、`OrderService`）。
- 包含复杂业务逻辑的类（如 `PaymentService` 处理支付流程）。

```
// 业务逻辑层，用 @Service 标识
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository; // 依赖数据访问层

    // 处理用户注册业务逻辑
    public void register(User user) {
        // 校验、加密密码等业务操作
        userRepository.save(user); // 调用数据访问层保存数据
    }
}
```

**3.** `**@Controller**`**：控制层注解**

`@Controller` 也是 `@Component` 的衍生注解，专门用于**控制层（Controller 层）** 的类，语义上表示 “接收用户请求、返回响应”。

**核心职责：**

- 接收 HTTP 请求（通过 `@RequestMapping`、`@GetMapping` 等注解映射）。
- 处理请求参数（如表单提交、URL 路径参数）。
- 调用 Service 层处理业务逻辑，最终返回视图或数据（如 JSON）。

**适用场景：**

- Spring MVC 中的控制器类（如 `UserController`、`OrderController`）。
- 负责接收前端请求并协调处理的类。=

```
// 控制层，用 @Controller 标识
@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService; // 依赖业务逻辑层

    // 接收用户注册请求
    @PostMapping("/register")
    public String register(User user) {
        userService.register(user); // 调用 Service 处理业务
        return "redirect:/login"; // 返回视图
    }
}
```

**特殊说明：**

- 若控制器需要返回 JSON 数据（如前后端分离项目），通常会配合 `@ResponseBody` 使用，或直接使用 `@RestController`（`@Controller + @ResponseBody` 的组合注解）。

**共性与区别总结**

| **注解**      | **本质**            | **语义 / 适用层**     | **核心作用**                         |
| ------------- | ------------------- | --------------------- | ------------------------------------ |
| `@Component`  | 通用组件注解        | 无明确分层            | 标识任意需要 Spring 管理的类         |
| `@Service`    | `@Component` 的衍生 | 业务逻辑层（Service） | 标识处理核心业务逻辑的类             |
| `@Controller` | `@Component` 的衍生 | 控制层（Controller）  | 标识接收 HTTP 请求、处理用户交互的类 |

#### @Resource和@Autowired的核心区别

**来源不同：**@Autowired是Spring框架自带注解；@Resource是JDK自带（Java EE规范）注解，不依赖Spring。

**匹配规则不同：**@Autowired默认按类型（type） 匹配，需配合@Qualifier按名称匹配；@Resource默认按名称（name） 匹配，名称失败才按类型匹配。

**依赖要求不同：**@Autowired默认要求依赖必须存在（可通过required=false关闭）；@Resource无强制依赖要求，找不到依赖时注入null。

**例子：**

## 注解

@Bean 将方法返回的对象注册为 Spring IoC 容器中的一个 Bean 实例，由 Spring 容器统一管理生命周期

@Configuration 将当前类标识为一个配置类

@ComponentScan 扫描组件

@Override 指示一个方法声明旨在**重写**（Override）父类（超类）中的方法或实现接口中的方法

@EnableWebMvc 用于启用和定制Spring MVC默认配置的模块驱动注解，放在类上

@ControllerAdvice 将当前类标识为异常处理的组件

@ExceptionHandler 用于设置所标识方法处理的异常

@Data 在 Lombok 中，@Data是一个组合注解

@Resource 是Java EE中的依赖注入注解，用于从容器（如Spring）中获取已实例化的Bean并注入到当前类的属性、构造方法或setter方法中，实现组件间解耦

@mapper 这个注解表示这是一个 mybatis 的 mapper 类

@JsonFormat 在 JSON 序列化（对象转 JSON 字符串）和反序列化（JSON 字符串转对象）时，自定义日期、时间、数字等类型的格式

@PathVariable 将URL 路径中的占位符参数绑定到控制器方法的参数上**，****方法的参数如果和路径参数不同名，就要加括号双引号指明取的是哪个路径参数@PathVariable("status") ；如果同名，就不用加**

@AutoFill 是 自定义注解（非 JDK 或 Spring 内置注解），核心作用是 自动填充实体类的指定字段

@Pointcut 里面写的是对哪些方法进行拦截，是 **Spring AOP（面向切面编程）**中的核心注解，作用是 **定义「切入点」**—— 简单说就是标记「哪些方法 / 类需要被 AOP 拦截」，是连接「切面逻辑」（如日志、权限校验、事务）和「目标业务逻辑」的桥梁

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