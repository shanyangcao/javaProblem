## Problem

## springmcv.xml

#### 扫描组件 

#### 视图解析器

#### view-controller

##### 主要作用：

快速页面跳转：当某个请求（如 `/home`、`/login`）不需要处理业务数据，只需直接返回一个视图（如 JSP、HTML 页面）时，无需编写 Controller 类和方法，通过 `view-controller` 即可完成映射。

简化配置：替代了 “定义 Controller 方法 + 返回视图名” 的冗余代码，直接在配置中完成请求与视图的绑定。

##### 配置方式：

**1. XML 配置（传统方式）：**

在 SpringMVC 的 XML 配置文件中，通过 `<mvc:view-controller>` 标签配置：

```
<!-- 将 /index 请求直接映射到 index 视图（如 index.jsp） -->
<mvc:view-controller path="/index" view-name="index"/>
```

**2. 注解配置（Java 配置类）：**

在 `@Configuration` 标注的 SpringMVC 配置类中，通过 `WebMvcConfigurer` 的 `addViewControllers` 方法配置：

```
@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 访问 /home 直接跳转到 home 视图
        registry.addViewController("/home").setViewName("home");
        // 访问 /about 直接跳转到 about 视图
        registry.addViewController("/about").setViewName("about");
    }
}
```

#### default-servlet-handler

当 DispatcherServlet 收到一个请求时，会先检查是否有对应的控制器（Controller）可以处理该请求。如果没有找到合适的控制器，`default-servlet-handler` 会将请求转发给 Web 容器（如 Tomcat）的默认 Servlet 来处理，而默认 Servlet 正是专门用于处理静态资源的
mvc注解驱动

#### 文件上传解析器

引入 `commons-fileupload` 依赖，是为了让 Spring MVC 能够使用 `CommonsMultipartResolver` 处理器来解析文件上传请求

##### 替代方案

从 Servlet 3.0 开始，Servlet 规范本身提供了文件上传支持，Spring MVC 也提供了对应的处理器 `StandardServletMultipartResolver`

#### 异常处理

#### 拦截器

1. `<property name="exceptionMappings">`

作用：配置异常类型与视图名的映射关系，即当指定类型的异常发生时，Spring MVC 会自动跳转到对应的视图进行处理

\2. `<property name="exceptionAttribute" value="ex">`

作用：指定在异常处理视图中，用于访问异常对象的属性名，在跳转后的视图（如上述`error`视图）中，可以通过`ex`这个变量名获取到异常对象

简单来说，`exceptionMappings`决定 “异常发生后跳转到哪个页面”，`exceptionAttribute`决定 “在跳转后的页面中用什么变量名访问异常对象”