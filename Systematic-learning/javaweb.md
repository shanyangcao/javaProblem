## Problem

#### 在javaweb中，当程序出现问题抛出异常，如何才能跳转到错误页面

在web.xml配置<error-page>

1. 按异常类型跳转


2. 按 HTTP 状态码跳转

#### AJAX局部更新

本质是**一种在不刷新整个页面的情况下，与后端服务器异步交换数据的技术，**地址栏不改变，不改变原页面布局

#### 同步和异步的区别

| **模式**                 | **核心特点**                                           | **执行流程**                                                 | **形象比喻**                                                 |
| ------------------------ | ------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **同步（Synchronous）**  | 任务顺序执行，前一个没完成，后一个必须等待             | 任务 A → 等待 A 完成 → 任务 B → 等待 B 完成 → 任务 C         | 排队买奶茶：必须等前一个人点完、拿到奶茶，下一个人才能上前点单 |
| **异步（Asynchronous）** | 任务无需等待前一个完成，各任务独立执行，完成后通知结果 | 任务 A 开始 → 直接开始任务 B → 任务 A 完成后通知 → 任务 B 完成后通知 | 点外卖：下单后不用等外卖员送，期间可以做其他事（如看电视），外卖到了会打电话通知 |

#### json-javabean和json字符串相互转换

#### 工具类作用

工具类的核心是 “封装通用功能以实现代码复用”

具体来说：

- 在**JDBC**中，可能会有`JDBCUtils`这样的工具类，封装数据库连接的获取、关闭等重复操作
- 在**JavaWeb**中，可能会有`WebUtils`这样的工具类，封装请求参数解析、会话处理等 Web 相关的通用逻辑
- 甚至在普通 JavaSE 程序中，也会有`DateUtils`（日期处理）、`MathUtils`（数学计算）等工具类

要求

工具类一般不new对象，会将构造函数私有化

所有方法都是静态的，直接用类名调用

#### `filterChain.doFilter(servletRequest, servletResponse);` 方法的主要作用是触发过滤器链中后续过滤器的执行

#### filter是java.servlet类下的

#### 什么是filter是前置代码，后置代码

在dofilter方法前面的代码，是前置代码，后面是后置

#### 矩阵变量是什么

`http://xxx/user;id=1;name=张三`这种在分号后面的 `id=1`、`name=张三` 就叫 “矩阵变量”

#### 如何修改MVC忽略；后面内容的规则

创建`UrlPathHelper`（URL 路径处理工具）。

用 `setRemoveSemicolonContent(false)` 告诉它：“别删分号后面的内容，留着！”

#### 什么时候会触发转换器 

假设前端发了一个请求：`http://localhost:8080/test?pet=旺财,2`

后端控制器方法是：

```
@GetMapping("/test")
public String test(Pet pet) { ... }
```

于是 Spring 会自动找有没有`String`转`Pet`的转换器（就是你定义的那个），找到后就调用它，把`"旺财,2"`转成`Pet`对象，再传给控制器方法

#### 什么时候触发过滤器

所有发往后端的 HTTP 请求，在到达控制器之前，都会先经过过滤器；处理完的响应在返回给前端之前，也会再经过过滤器

#### 什么时候触发解析器

需要处理请求参数时，参数解析器 / 消息转换器触发；

需要处理响应数据时，消息转换器触发；

需要找到视图页面时，视图解析器触发；

需要解析 URL 中的变量时，路径变量解析器触发

#### 什么时候触发拦截器

假设你访问 `/user/detail` 接口：

请求先经过过滤器（比如登录过滤器），通过后进入 Spring MVC 框架。

**拦截器第一步触发**：在调用 `UserController` 的 `detail()` 方法前执行（比如检查用户权限）。

控制器方法 `detail()` 执行，处理业务逻辑。

**拦截器第二步触发**：控制器处理完，还没渲染 `detail.html` 页面时执行（比如给模型里加一个当前时间）。

视图渲染完成（`detail.html` 生成好）。

**拦截器第三步触发**：记录这次请求从开始到结束用了多少毫秒

**巩固知识**

**执行顺序示意图**

```
前端（浏览器/APP）
    ↓
1. 【过滤器（Filter）】→ 请求进入后端的第一道“关卡”（先于所有Spring组件）
    ↓
2. Spring MVC的DispatcherServlet（前端控制器，请求的“分发中心”）
    ↓
3. 【拦截器 - 前置方法】→ 控制器处理前执行（如登录校验）
    ↓
4. 【控制器（Controller）】→ 核心业务逻辑处理（如调用Service、返回数据）
    ↓
5. 【拦截器 - 后置方法】→ 控制器处理后、视图渲染前执行（如补充数据）
    ↓
6. 【视图渲染】（如Thymeleaf/JS渲染页面，若返回JSON则跳过此步）
    ↓
7. 【拦截器 - 完成方法】→ 视图渲染后、响应返回前执行（如记录耗时）
    ↓
8. 【过滤器（Filter）】→ 响应返回前端前的最后一道“关卡”（后置处理）
    ↓
前端（接收响应，展示页面/数据）
```

## web.xml

### servlet

### filter

主要用两个过滤器：`CharacterEncodingFilter` ，`HiddenHttpMethodFilter` 

#### `CharacterEncodingFilter` 

有三个属性encoding，forceRequestEncoding，forceResponseEncoding

forceResponseEncoding强制设置响应的编码格式。若为 true，响应的编码会被强制设置为 encoding 的值

#### `HiddenHttpMethodFilter` 

只有一个核心属性：`string类型的methodParam`，默认值是`_method`

`**doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)**`作用：过滤器的核心逻辑实现。

流程：
① 检查请求方法是否为 `POST`；
② 从请求参数中获取 `methodParam` 指定的参数值（如 `_method=PUT`）；
③ 将原 `POST` 请求包装为一个新的请求对象，其方法为参数值指定的 HTTP 方法（如 `PUT`）；
④ 将包装后的请求传递给后续过滤器或 Servlet。

这个过滤器会检查收到的`POST`请求里，有没有一个隐藏的参数（比如 `_method=DELETE`）。如果发现了，它就会把这个`POST`请求“伪装”或“转换”成一个`DELETE`请求，再交给后续的程序处理。

这两个过滤器共同作用合起来的意思就是：

**“每当有请求来到我们的网站时，先让‘统一语言解码器’过一遍，确保所有文字不会乱码；然后再让‘方法转换器’过一遍，看看是不是一个伪装了的特殊请求（比如删除请求）。这两个插件都处理完后，再把请求交给真正的业务逻辑去处理。”**

### listener

### 配置spring

Spring 的 IoC 容器（管理业务层 Bean）通常通过 `ContextLoaderListener`（监听器） 初始化，需要在 `web.xml` 中指定其配置类（或 XML）：

```
<!-- 配置 Spring 根容器的监听器 -->
<listener>
  <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>

<!-- 指定 Spring 根容器的配置类（或 XML 文件） -->
<context-param>
  <param-name>contextConfigLocation</param-name>
  <!-- 若用注解配置类，需指定类的全路径 -->
  <param-value>com.example.config.SpringConfig</param-value>
  <!-- 若用 XML 配置，則指定 XML 路径：classpath:spring-core.xml -->
</context-param>
```

`ContextLoaderListener` 会在 Web 容器启动时执行，通过 `contextConfigLocation` 找到 Spring 的配置类，初始化 IoC 容器（根容器）。

### 配置springmvc

SpringMVC 的核心是 `DispatcherServlet`（处理 Web 请求），需要在 `web.xml` 中配置该 Servlet，并指定其配置类（或 XML）：

```
<!-- 配置 SpringMVC 的前端控制器 DispatcherServlet -->
<servlet>
    <servlet-name>springmvc</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    
    <!-- 指定 SpringMVC 的配置类（或 XML 文件） -->
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <!-- 配置类全路径 -->
        <param-value>com.example.config.SpringMvcConfig</param-value>
        <!-- 若用 XML：classpath:spring-mvc.xml -->
    </init-param>
    
    <!-- 让 DispatcherServlet 在 Web 容器启动时就初始化 -->
    <load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
    <servlet-name>springmvc</servlet-name>
    <url-pattern>/</url-pattern> <!-- 拦截所有请求 -->
</servlet-mapping>
```

`DispatcherServlet` 初始化时，会通过 `contextConfigLocation` 找到 SpringMVC 的配置类，初始化 MVC 子容器（管理 Controller 等 Web 层 Bean）。

### 为什么必须指定配置类？

- 框架无法 “自动猜测” 你的配置：比如哪些包需要扫描、是否开启注解驱动、如何配置视图解析器等，必须通过配置类明确告知。
- 分离职责：Spring 根容器（业务层）和 SpringMVC 子容器（Web 层）的配置通常是分离的（如 Spring 配置数据源，SpringMVC 配置拦截器），通过不同的配置类管理更清晰。

### 补充：现代项目的简化方式

在 Servlet 3.0+ 环境中，可以通过 **注解驱动** 替代 `web.xml`（如使用 `@WebListener`、`@WebServlet`），甚至通过 Spring Boot 的自动配置进一步简化。但核心逻辑不变：**框架仍需要知道配置信息的位置，只是从 “在 web.xml 中显式指定” 变成了 “约定优于配置” 或 “注解指定”**。