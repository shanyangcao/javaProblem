## Problem

#### 一个数据库只有一个SqlSessionFactory对象

#### 数据源是获取connection对象的

#### 事务管理器最好是定义一个接口，然后每一个具体的事务管理器都实现这个接口

#### 提取数据源、事务管理器和SQL映射信息，然后将这些信息组装成SqlSessionFactory对象

### mybatis-config.xml

`mybatis-config.xml` 是 **MyBatis 框架的核心全局配置文件**，用于配置 MyBatis 的运行环境、核心参数、映射关系等基础设置，是 MyBatis 框架初始化的入口配置。它的主要作用是告诉 MyBatis 如何连接数据库、如何处理映射关系、使用哪些插件等，是 MyBatis 运行的 “总开关”

#### 配置内容

##### 1. 配置数据库环境（environments）

定义数据库连接信息（如驱动、URL、用户名、密码）和事务管理方式，支持多环境配置（如开发、测试、生产环境），方便切换。

示例：

##### 2. 注册映射文件（mappers）

告诉 MyBatis 哪里存放 Mapper 映射文件（如 `CarMapper.xml`、`UserMapper.xml`），或直接注册 Mapper 接口，让 MyBatis 能找到 SQL 语句的定义。

常见方式：

##### 3. 配置类型别名（typeAliases）

为 Java 类的全限定名定义简短别名，简化 Mapper.xml 中的类名引用（无需写完整包路径）。

示例：

##### 4. 设置全局参数（settings）

配置 MyBatis 的核心运行规则，如缓存开关、日志实现、驼峰命名自动映射等，影响框架整体行为。

常用设置示例：

##### 5. 配置类型处理器（typeHandlers）

自定义 Java 类型与数据库字段类型的转换规则（如将 Java 的 `LocalDateTime` 与数据库的 `DATETIME` 类型映射）。

##### 6. 配置插件（plugins）

集成第三方插件（如分页插件 PageHelper、SQL 打印插件等），增强 MyBatis 功能。

示例（集成 PageHelper）：

### XXMapper.xml

 **MyBatis 框架**中用于映射与 `XX` 实体类相关的数据库操作的配置文件，主要作用是定义 SQL 语句、结果映射规则，以及将 Java 方法与数据库操作关联起来，实现对象（Java）与关系（数据库表）的映射（ORM）

#### 配置内容

\1. 命名空间（namespace）

2.CRUD 操作的 SQL 标签

\3. 结果映射（resultMap）

当数据库表字段名与 Java 实体类的属性名不一致时，通过 `resultMap` 定义映射关系（避免手动在 SQL 中写别名）

\4. 动态 SQL

### SqlSession 

`SqlSession` 是**与数据库交互的核心接口**，它封装了数据库连接（`Connection`），提供了执行 SQL 语句、管理事务、获取 Mapper 接口等功能，简单说：`SqlSession` 就像 Java 程序与数据库之间的 “对话通道”

#### 常用方法

#### 1. **获取 Mapper 接口（推荐）**

通过 `getMapper(Class<T> type)` 方法获取 Mapper 接口的代理对象，后续直接调用接口方法即可执行对应 SQL

#### 2. **直接执行 SQL 语句（基于 statementId）**

crud

#### 3. **事务管理**

`SqlSession` 默认开启事务且手动提交（需显式调用 `commit()`），支持事务控制

#### 4. **关闭会话**

`close()`：释放数据库连接等资源，必须在使用后调用（建议放在 `finally` 块中）