## Problem

pom.xml是maven的核心配置文件：

1.项目基本信息：GAV公司项目版本，SpringMVC 是 Web 项目，必须配置 <packaging>war</packaging>

2.依赖配置：项目开发中需要用到各种框架（比如 SpringMVC、Spring 核心）、工具包（比如数据库驱动、JSON 解析包），这些都叫 “依赖”

3.配置项目的构建规则（用什么 JDK 编译、打包成什么格式、资源文件放哪）