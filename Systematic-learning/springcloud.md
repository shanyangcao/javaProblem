## problem

#### Maven中的DependencyManagement和Dependencies区别

在 Maven 中，`DependencyManagement`和`Dependencies`都是管理依赖的核心配置，但作用和场景完全不同：

**1. Dependencies（直接依赖）**

- **作用**：**实际引入依赖**，会将配置的依赖下载到项目中，并参与编译、打包等构建过程。
- **特点**：


- - 写在`dependencies`标签下的依赖，会直接生效（项目会真正引入这个 Jar 包）。
  - 子模块会**继承父模块的**`**dependencies**`**依赖**（无需重复配置）。


- **场景**：项目需要直接使用的依赖（比如 Spring Boot 的`spring-boot-starter-web`）。

**2. DependencyManagement（依赖管理）**

- **作用**：**统一管理依赖的版本、范围等信息，但不实际引入依赖**（仅做 “声明”）。
- **特点**：


- - 写在`dependencyManagement`标签下的依赖，**不会自动下载到项目**，需要在`dependencies`中再次声明才会生效。
  - 子模块可以**继承父模块**`**dependencyManagement**`**中定义的依赖版本**（子模块声明时无需写版本号，统一由父模块管理）。


- **场景**：多模块项目中统一管理依赖版本（避免子模块版本不一致），或提前声明可能用到的依赖。

**举个例子**

**父模块的**`pom.xml`**：**

```
<!-- 父模块：用dependencyManagement统一管理版本 -->
<dependencyManagement>
  <<dependencies>
    <!-- 仅声明版本，不实际引入 -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
      <version>2.7.0</version>
    </dependency>
  </</dependencies>
</dependencyManagement>
```

**子模块的**`**pom.xml**`**:**

```
<!-- 子模块：从父模块继承版本，实际引入依赖 -->
<<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <!-- 无需写version，自动用父模块的2.7.0 -->
  </dependency>
</</dependencies>
```

##### 核心区别总结

| **维度**         | **Dependencies**        | **DependencyManagement**       |
| ---------------- | ----------------------- | ------------------------------ |
| 是否实际引入依赖 | ✅ 是（直接下载 Jar 包） | ❌ 否（仅声明信息）             |
| 子模块继承行为   | ✅ 子模块自动继承依赖    | ❌ 子模块需手动声明才会引入     |
| 核心作用         | 引入项目需要的依赖      | 统一管理依赖的版本、范围等信息 |

#### restTemplate什么作用

简单来说：`**RestTemplate**` **是 Spring 框架提供的一个同步 HTTP 客户端工具，专门用于在 Java 程序中发送 HTTP 请求（如 GET/POST/PUT/DELETE），并便捷地处理响应结果，无需手动编写繁琐的 HTTP 连接、请求参数拼接、响应解析等代码**。

它就像 Java 程序的 “浏览器”，帮助后端服务之间（如微服务间通信）或后端调用第三方 HTTP 接口（如调用微信支付、天气 API）时，简化 HTTP 交互流程。

**一、核心作用详解**

**1. 简化 HTTP 请求编写，告别原生 JDBC 式繁琐代码**

原生 Java 发送 HTTP 请求需要使用 `HttpURLConnection` 或 `HttpClient`，需要手动处理连接建立、参数拼接、流读取、异常捕获等，代码冗余且易出错。`RestTemplate` 封装了这些底层细节，通过简洁的 API 即可完成请求发送，大幅减少样板代码。

**对比：原生 HttpClient vs RestTemplate**

- 原生 HttpClient 发送 GET 请求（繁琐）：

```
// 需手动创建客户端、请求、处理响应，代码量大
CloseableHttpClient client = HttpClients.createDefault();
HttpGet request = new HttpGet("https://api.example.com/user/1");
CloseableHttpResponse response = client.execute(request);
// 手动读取响应流、解析数据...
```

- RestTemplate 发送 GET 请求（简洁）：

```
RestTemplate restTemplate = new RestTemplate();
// 一行代码完成请求+响应解析
User user = restTemplate.getForObject("https://api.example.com/user/1", User.class);
```

**2. 自动完成请求 / 响应的序列化与反序列化**

`RestTemplate` 内置了消息转换器（`HttpMessageConverter`），支持将 Java 对象自动转换为 JSON/XML 格式的请求体（如 POST 请求传递 JSON 参数），也能将响应的 JSON/XML 自动转换为 Java 实体类，无需手动使用 FastJSON、Jackson 等工具进行序列化操作。

**示例：发送 POST 请求（自动序列化 / 反序列化）**

```
// 1. 准备请求参数（Java 对象）
UserRequest requestParam = new UserRequest("张三", 25);
// 2. 发送 POST 请求，自动将 requestParam 转为 JSON，响应自动转为 User 对象
RestTemplate restTemplate = new RestTemplate();
User user = restTemplate.postForObject(
    "https://api.example.com/user/add",  // 请求地址
    requestParam,                        // 请求体（自动转 JSON）
    User.class                           // 响应类型（自动反序列化）
);
```

**3. 支持多种 HTTP 请求方法，覆盖常见场景**

`RestTemplate` 封装了 HTTP 所有常用请求方法，满足不同业务需求：

| **HTTP 方法** | **RestTemplate 对应方法**                               | **用途**                 |
| ------------- | ------------------------------------------------------- | ------------------------ |
| GET           | `getForObject()`/`getForEntity()`                       | 查询数据（获取资源）     |
| POST          | `postForObject()`/`postForEntity()`/`postForLocation()` | 创建数据（提交资源）     |
| PUT           | `put()`                                                 | 更新数据（全量更新）     |
| DELETE        | `delete()`                                              | 删除数据（删除资源）     |
| HEAD          | `headForHeaders()`                                      | 获取响应头信息           |
| OPTIONS       | `optionsForAllow()`                                     | 获取接口支持的 HTTP 方法 |

其中，`getForEntity()`/`postForEntity()` 比 `getForObject()`/`postForObject()` 更强大，能获取完整的响应信息（状态码、响应头、响应体），便于处理异常场景。

**4. 可配置与扩展，适配复杂场景**

`RestTemplate` 支持自定义配置，满足特殊需求：

- 配置请求头：设置 Token、Content-Type 等（如接口认证、指定请求格式）；
- 配置超时时间：避免请求长时间阻塞；
- 替换消息转换器：如使用 FastJSON 替代默认的 Jackson 进行序列化；
- 配置拦截器：统一处理日志记录、请求签名等通用逻辑。

**示例：自定义配置 RestTemplate（设置超时 + 请求头）**

```
// 1. 配置 HTTP 连接池与超时时间
SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
factory.setConnectTimeout(5000);  // 连接超时 5 秒
factory.setReadTimeout(10000);    // 读取超时 10 秒

// 2. 创建 RestTemplate 实例
RestTemplate restTemplate = new RestTemplate(factory);

// 3. 统一设置请求头（如认证 Token）
HttpHeaders headers = new HttpHeaders();
headers.set("Authorization", "Bearer xxxxxx");
headers.setContentType(MediaType.APPLICATION_JSON);

// 4. 发送请求
HttpEntity<UserRequest> requestEntity = new HttpEntity<>(requestParam, headers);
ResponseEntity<User> response = restTemplate.postForEntity(
    "https://api.example.com/user/add",
    requestEntity,
    User.class
);

// 获取响应状态码、响应体
if (response.getStatusCode().is2xxSuccessful()) {
    User result = response.getBody();
}
```

## Note

- 口诀：建module，改pom，写yaml，主启动，业务类
- 引入依赖并配置地址，就是在注册服务
- 消费者只有controller，提供者才有service和dao