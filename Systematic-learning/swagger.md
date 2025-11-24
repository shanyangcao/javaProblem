## Note

- @ApiModel注释类，@ApiModelProperty注释属性，@ApiParam注释参数

## swagger注解

![img](https://cdn.nlark.com/yuque/0/2025/png/56763514/1763864822266-da090c4e-2d2b-4563-8a62-f39609dfc3e2.png)

## swagger使用步骤

### 一、引入 Swagger 依赖

在 `pom.xml`（Maven 项目）中添加以下依赖：

```
<!-- Swagger 核心依赖 -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.9.2</version>
</dependency>
<!-- Swagger UI 依赖（用于可视化界面） -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.9.2</version>
</dependency>
```

**若使用 Swagger 3（OpenAPI 3），可替换为** `**springfox-boot-starter**` **依赖，版本选择** `**3.0.0**` **及以上**。

### 二、编写 Swagger 配置类

创建 `SwaggerConfig.java` 配置类，用于开启 Swagger 并配置文档信息：

```
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2 // 开启 Swagger 2 功能（Swagger 3 可省略此注解）
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2) // 指定文档类型（Swagger 3 用 DocumentationType.OAS_30）
                .apiInfo(apiInfo()) // 配置文档基本信息
                .select()
                // 扫描指定包下的接口（替换为你的 Controller 包路径）
                .apis(RequestHandlerSelectors.basePackage("com.example.controller"))
                .paths(PathSelectors.any()) // 匹配所有路径
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("苍穹外卖项目接口文档") // 文档标题
                .description("项目接口的详细说明") // 文档描述
                .version("2.0") // 文档版本
                .build();
    }
}
```

### 三、在 Controller 和 Model 中添加 Swagger 注解

通过注解为接口和实体类添加说明，让文档更清晰：

**1. Controller 层注解示例**

```
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employee")
@Api(tags = "员工管理接口") // 给 Controller 分组并添加说明
public class EmployeeController {

    @PostMapping("/login")
    @ApiOperation("员工登录") // 给接口添加说明
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO dto) {
        // 业务逻辑...
    }
}
```

**2. 实体类注解示例**

```
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("员工登录DTO") // 给实体类添加说明
public class EmployeeLoginDTO {

    @ApiModelProperty("用户名") // 给字段添加说明
    private String username;

    @ApiModelProperty("密码")
    private String password;
}
```

### 四、访问 Swagger 文档

启动 Spring Boot 项目后，通过以下地址访问 Swagger UI 界面：

- Swagger 2：`http://localhost:8080/swagger-ui.html`
- Swagger 3：`http://localhost:8080/swagger-ui/index.html`
- 若配置了自定义路径（如你提到的 `doc.html`），则访问 `http://localhost:8080/doc.html`