## Problem

#### 转换器都有什么功能

转换器（Converter） 的核心功能可以通俗理解为：比如把前端传的字符串转成后端需要的对象

\1. 前端请求数据 → 后端参数：解决 “格式不匹配”

\2. 后端内部数据转换：解决 “逻辑适配”

\3. 后端响应数据 → 前端 / 存储：解决 “输出格式统一”

转换器的 2 个关键特性：自动触发，不用手动调用；支持自定义

## ConfigurableApplicationContext

- **组件扫描**：从 `MainApplication.class` 所在的包开始，递归扫描所有带有 `@Component` 及其派生注解（`@Controller`、`@Service`、`@Repository` 等）的类，将它们创建为 “Bean” 并注册到容器中
- **自动配置**：根据引入的依赖（如 `spring-boot-starter-web`、`spring-boot-starter-data-jpa`），自动加载对应的 “自动配置类”（如 `WebMvcAutoConfiguration` 配置 Spring MVC、`DataSourceAutoConfiguration` 配置数据库连接池），无需手动编写 XML 或 Java 配置
- **Bean 的依赖注入**：容器会解析 Bean 之间的依赖关系（如 `@Autowired` 标注的属性或构造方法），自动将依赖的 Bean 注入到目标对象中（例如将 `UserService` 注入到 `UserController`）