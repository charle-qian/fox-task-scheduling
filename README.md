# fox-task-scheduling

一个基于 Java 21 和 Spring Boot 4.1 的任务调度管理框架起始工程，内置任务定义、优先级、限流配置、动态启停、执行记录、Swagger API 文档和 Thymeleaf 管理后台。

## 技术栈

- Java 21
- Spring Boot 4.1
- Spring Boot Virtual Threads
- Spring MVC + Thymeleaf
- Spring Security
- Spring Data JPA + H2
- Quartz
- springdoc-openapi / Swagger UI
- Spring Boot Actuator

## 本地运行

```powershell
.\mvnw.cmd spring-boot:run
```

应用默认启动在 `http://localhost:8080`。

## 管理后台

- 登录页：`http://localhost:8080/login`
- Dashboard：`http://localhost:8080/admin`
- 任务管理：`http://localhost:8080/admin/tasks`
- 执行记录：`http://localhost:8080/admin/executions`

默认本地账号：

- 用户名：`admin`
- 密码：`admin123`

可以通过 `app.security.admin-password` 修改默认密码。

## API 与调试入口

- Swagger UI：`http://localhost:8080/swagger-ui.html`
- OpenAPI JSON：`http://localhost:8080/v3/api-docs`
- H2 Console：`http://localhost:8080/h2-console`
- Actuator Health：`http://localhost:8080/actuator/health`

H2 JDBC URL：

```text
jdbc:h2:mem:fox_task_scheduling
```

## 常用命令

```powershell
.\mvnw.cmd test
.\mvnw.cmd -DskipTests package
```

## 当前能力

- 任务定义 CRUD API
- 任务启用、禁用、暂停、恢复
- 手动触发任务并生成执行记录
- Quartz Cron 调度基础集成
- 本地 no-op handler 执行闭环
- 登录保护的 Thymeleaf 管理页面
- 统一 API 错误响应
