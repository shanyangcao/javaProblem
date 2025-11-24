## Problem

#### 转换器都有什么功能

转换器（Converter） 的核心功能可以通俗理解为：比如把前端传的字符串转成后端需要的对象

\1. 前端请求数据 → 后端参数：解决 “格式不匹配”

\2. 后端内部数据转换：解决 “逻辑适配”

\3. 后端响应数据 → 前端 / 存储：解决 “输出格式统一”

转换器的 2 个关键特性：自动触发，不用手动调用；支持自定义

#### web依赖有什么

- **Tomcat**提供运行环境
- **DispatcherServlet**负责请求分发（Spring MVC 的核心）
- **XML**用于配置项目参数

#### mybatis配置具体含义

![img](https://cdn.nlark.com/yuque/0/2025/png/56763514/1762529020579-6283b500-fd1c-4cd3-8e92-c34d29f3d68b.png)

这是 Spring Boot 中 **MyBatis 的核心配置**，用于告诉 MyBatis 如何找到你的实体类（POJO）和 Mapper 映射文件（XML），简化开发时的代码编写。下面逐行拆解含义：

**1.** `**mybatis.type-aliases-package: com.guo.pojo**`

**作用：指定「实体类（POJO）的包路径」，让 MyBatis 自动给实体类起「别名」**

- **背景**：MyBatis 在 XML 映射文件（或注解）中使用实体类时，默认需要写「全类名」（如 `com.guo.pojo.User`），繁琐且易出错。
- **配置后效果**：MyBatis 会扫描 `com.guo.pojo` 包下的所有类，自动将「类名」作为「别名」（不区分大小写，推荐首字母小写）。

**示例对比：**

- 未配置时（需写全类名）：

```
<!-- XML 中查询返回 User 实体，需写全类名 -->
<select id="getUserById" resultType="com.guo.pojo.User">
  select * from user where id = #{id}
</select>
```

- 配置后（可直接用别名）：

```
<!-- 直接用类名 User 或 user 作为别名，简洁很多 -->
<select id="getUserById" resultType="User">
  select * from user where id = #{id}
</select>
```

**注意：**

- 别名默认是「类名」（如 `User` → 别名 `user` 或 `User`，MyBatis 不区分大小写）。
- 若包下有重名类（如 `com.guo.pojo.User` 和 `com.guo.pojo.admin.User`），需手动通过 `@Alias` 注解指定唯一别名：

```
// 在 POJO 类上添加注解，指定别名
@Alias("userInfo")
public class User { ... }
```

**2.** `mybatis.mapper-locations: classpath:mybatis/mapper/*.xml`

**作用：指定「MyBatis Mapper 映射文件（XML）的存放路径」，让 MyBatis 自动加载这些文件**

**核心概念：**

- - `classpath:`：表示从项目的「类路径」下查找（Maven 项目中，`src/main/resources` 目录就是类路径根目录）。
  - `mybatis/mapper/*.xml`：匹配「类路径下 `mybatis/mapper` 文件夹中所有 `.xml` 后缀的文件」。


- **为什么需要**：Mapper 映射文件是用来写 SQL 语句的（如查询、新增、修改），MyBatis 必须找到这些文件才能执行 SQL，否则会报「绑定失败」错误（如 `BindingException: Invalid bound statement (not found)`）。

**项目目录结构示例（需对应配置路径）：**

```
src/main/resources/  # 类路径根目录
└── mybatis/
    └── mapper/      # 映射文件存放目录
        ├── UserMapper.xml  # 符合 *.xml 匹配规则，会被加载
        ├── OrderMapper.xml # 同样会被加载
        └── other/
            └── GoodsMapper.xml # 不会被加载（路径不匹配）
```

**补充：**

- 若映射文件放在 `src/main/resources/mapper/*.xml`，配置需改为 `classpath:mapper/*.xml`（路径要和实际存放位置一致）。
- 若使用「纯注解开发」（不写 XML，直接在 Mapper 接口上用 `@Select`、`@Insert` 等注解），可省略该配置；但如果是「XML + 接口」混合开发，必须配置该路径。

#### 如何启动异步

①想办法告诉spring我们的异步方法是异步的，所以要在方法上添加注解

②去springboot主程序中开启异步注解功能（@EnableAsync）

#### 异步任务，邮件任务，定时任务的学习重点是什么

| 任务类型     | Spring Boot 中的定位 | 学习重点                                      |
| ------------ | -------------------- | --------------------------------------------- |
| **异步任务** | **应用性能优化**     | `@Async`, `@EnableAsync`, 线程池配置          |
| **邮件任务** | **业务功能实现**     | `spring-boot-starter-mail`, `JavaMailSender`  |
| **定时任务** | **系统调度管理**     | `@Scheduled`, `@EnableScheduling`, cron表达式 |

#### result类什么作用

`Result<T>` 这个类是一个**通用的接口返回结果封装类**，在后端开发中非常常见，尤其是在 RESTful API 中。它的核心作用是**统一接口的返回格式**，让前端能够更方便、更规范地处理后端响应。

**核心作用**

1. **统一返回格式**：

- - 无论接口是成功还是失败，都会返回一个包含 `code`、`msg`、`data` 三个字段的 JSON 对象。
  - 这使得前端不需要针对不同的成功或失败情况编写不同的解析逻辑。

1. **明确的状态标识**：

- - `code` 字段用于标识请求的状态。
  - `1` 代表成功。
  - `0` 或其他数字代表失败，不同的数字可以对应不同的错误类型。
  - 前端可以通过判断 `code` 的值来快速确定请求是否成功。

1. **携带返回数据**：

- - 当请求成功时（`code=1`），`data` 字段会包含接口返回的具体业务数据。
  - 这个 `data` 字段的类型是泛型 `<T>`，这意味着它可以是任何类型的对象（比如一个用户信息对象、一个商品列表、一个数字等），使得这个 `Result` 类具有极高的灵活性和通用性。

1. **提供错误信息**：

- - 当请求失败时（`code!=1`），`msg` 字段会包含人类可读的错误信息。
  - 这个信息可以直接展示给用户，或者用于开发者调试。

**代码逐行解析**

- `@Data`: 这是 Lombok 注解，它会自动为这个类生成 `getter`、`setter`、`toString()`、`equals()` 和 `hashCode()` 等方法，让代码更简洁。
- `public class Result<T> implements Serializable`:


- - `Result<T>`: 声明这是一个泛型类，`T` 是一个类型占位符，可以在使用时指定具体的类型。
  - `implements Serializable`: 表示这个类的对象可以被序列化，这在某些场景下（如分布式系统、缓存）是必需的。


- `private Integer code;`: 状态码。
- `private String msg;`: 消息描述。
- `private T data;`: 泛型数据。

**静态工厂方法解析**

这些静态方法是 `Result` 类的核心，它们提供了一种优雅的方式来创建 `Result` 对象。

1. `public static <T> Result<T> success()`:

- - 用于创建一个**无数据返回的成功响应**。
  - 例如，一个删除操作，成功后不需要返回任何数据，只需要告知前端成功即可。
  - 它会创建一个 `Result` 对象，将 `code` 设置为 `1`。

1. `public static <T> Result<T> success(T object)`:

- - 用于创建一个**携带数据的成功响应**。
  - 这是最常用的方法。当查询或创建数据成功后，将获取到的数据对象作为参数传入。
  - 它会创建一个 `Result` 对象，将 `code` 设置为 `1`，并把传入的 `object` 赋值给 `data` 字段。

1. `public static <T> Result<T> error(String msg)`:

- - 用于创建一个**失败响应**。
  - 当业务逻辑校验失败、数据库操作出错或发生其他异常时，可以调用此方法。
  - 它会创建一个 `Result` 对象，将 `code` 设置为 `0`，并把传入的错误信息 `msg` 赋值给 `msg` 字段。
  - **注意**：这个方法的返回类型是 `Result<T>`，但它内部创建的是 `new Result()`。在 Java 中，这会被推断为 `Result<Object>`，然后向上转型为 `Result<T>`。这是一个小瑕疵，更严谨的写法应该是 `new Result<T>()`。不过在大多数情况下，由于类型擦除，这并不会导致问题。

**在实际应用中的例子**

假设你有一个用户控制器 `UserController`：

```
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user != null) {
            // 查询成功，返回用户数据
            return Result.success(user);
        } else {
            // 查询失败，返回错误信息
            return Result.error("用户不存在");
        }
    }

    @PostMapping
    public Result<User> createUser(@RequestBody User user) {
        boolean success = userService.save(user);
        if (success) {
            // 创建成功，返回创建后的用户信息
            return Result.success(user);
        } else {
            // 创建失败
            return Result.error("创建用户失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Integer id) {
        boolean success = userService.removeById(id);
        if (success) {
            // 删除成功，无需返回数据
            return Result.success();
        } else {
            return Result.error("删除用户失败");
        }
    }
}
```

**成功响应示例 (getUserById)**

```
{
  "code": 1,
  "msg": null,
  "data": {
    "id": 1,
    "username": "张三",
    "email": "zhangsan@example.com"
  }
}
```

**失败响应示例 (getUserById)**

```
{
  "code": 0,
  "msg": "用户不存在",
  "data": null
}
```

## Note

- Application程序本身就是个组件
- springboot依赖都是spring-boot-stater开头
- 最开始通过new对象创建对象，给对象赋值；学了spring就@component通过@value来赋值；再学springboot就通过Application.yaml来赋值，需要@ConfigurationProperties(prefix = "")来绑定对象
- xxxxAutoConfigurartion：自动配置类，绑定xxx类；给容器中添加组件，xxxxProperties:封装配置文件中相关属性
- 可以通过debug=true来判断哪些自动配置类生效了
- templates目录下的所有页面，只能通过controller来跳转
-  xxxconfiguration是来扩展springmvc，比如视图解析，视图跳转功能
- Spring 会自动扫描 `com.shuxuejia.managementsystem` 包下的所有类，以及子包（默认扫包）
- 自定义了国际化资源文件，因此我们需要在SpringBoot配置文件`application.properties`中加入以下配置指定我们配置文件的名称

```
spring.messages.basename=i18n.login
```

- Spring Boot 不仅提供了默认的数据源，同时默认已经配置好了 JdbcTemplate 放在了容器中，程序员只需自己注入即可使用
- springboot所有配置类都有一个自动配置类，xxxAutoconfiguration；自动配置类都会绑定一个properties配置文件

## ConfigurableApplicationContext

- **组件扫描**：从 `MainApplication.class` 所在的包开始，递归扫描所有带有 `@Component` 及其派生注解（`@Controller`、`@Service`、`@Repository` 等）的类，将它们创建为 “Bean” 并注册到容器中
- **自动配置**：根据引入的依赖（如 `spring-boot-starter-web`、`spring-boot-starter-data-jpa`），自动加载对应的 “自动配置类”（如 `WebMvcAutoConfiguration` 配置 Spring MVC、`DataSourceAutoConfiguration` 配置数据库连接池），无需手动编写 XML 或 Java 配置
- **Bean 的依赖注入**：容器会解析 Bean 之间的依赖关系（如 `@Autowired` 标注的属性或构造方法），自动将依赖的 Bean 注入到目标对象中（例如将 `UserService` 注入到 `UserController`）