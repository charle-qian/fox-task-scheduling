# Fox Task Scheduling Platform Design

## Goal

Build a runnable starting framework for `fox-task-scheduling`: a Java 21 Spring Boot task scheduling platform with Swagger API documentation and a Thymeleaf management console.

## Technology Choices

- Java: 21.
- Spring Boot: 4.1.x, selected because the current official Spring Boot system requirements support Java 21.
- Build: Maven.
- Web stack: Spring MVC, Thymeleaf, Jakarta Validation.
- API docs: springdoc-openapi WebMVC UI starter, exposing Swagger UI and `/v3/api-docs`.
- Security: Spring Security with form login for the admin console and basic protection for management APIs.
- Persistence: Spring Data JPA with H2 as the default local database.
- Scheduler: Quartz, integrated through Spring Boot.
- Operations: Spring Boot Actuator for health and runtime checks.
- Tests: JUnit, Spring Boot Test, MockMvc, and repository/service integration tests.

## Scope

The first version should be a complete local management platform skeleton, not only a bare API scaffold.

Included:

- Application bootstrap and Maven project structure.
- Admin login page.
- Thymeleaf dashboard.
- Task management page.
- Execution history page.
- Swagger UI entry.
- REST APIs for task definitions and execution records.
- Database-backed task definitions.
- Database-backed execution records.
- Quartz-backed scheduling registration, pause, resume, and manual trigger hooks.
- Seeded default admin user and sample task data for local development.
- Unified JSON error responses for API validation and not-found cases.
- Tests for the main API, service, persistence, and page routes.

Deferred:

- Distributed locking.
- Cluster scheduling coordination.
- Multi-tenant isolation.
- External worker execution protocol.
- Fine-grained role and permission management.
- Production authentication provider integration.
- MySQL migration scripts.
- Advanced retry, alerting, and workflow orchestration.

## Architecture

The application is a single Spring Boot service with clear package boundaries:

- `config`: framework configuration for OpenAPI, Security, Quartz, and data initialization.
- `task.domain`: JPA entities, enums, repositories, and domain-level value concepts.
- `task.application`: services that own task definition workflows and scheduler coordination.
- `task.web.api`: REST controllers, request/response DTOs, and API error handling.
- `task.web.admin`: Thymeleaf controllers for the management console.
- `execution.domain`: execution record entity, status enum, and repository.
- `execution.application`: execution recording and query service.
- `common`: shared errors, time helpers, and API response support when needed.

The web layer calls application services. Application services own transactions and coordinate repositories plus Quartz. Controllers do not access repositories directly.

## Data Model

### TaskDefinition

Fields:

- `id`: generated primary key.
- `code`: unique task code used by APIs and scheduler identity.
- `name`: display name.
- `description`: optional detail text.
- `cronExpression`: Quartz cron expression.
- `priority`: integer priority, higher value means higher scheduling priority.
- `rateLimitPerMinute`: optional per-minute limit.
- `status`: `ENABLED`, `DISABLED`, or `PAUSED`.
- `handlerName`: logical handler key for future executor integration.
- `createdAt`: creation timestamp.
- `updatedAt`: last update timestamp.

### TaskExecutionRecord

Fields:

- `id`: generated primary key.
- `taskCode`: task code snapshot.
- `taskName`: task name snapshot.
- `status`: `RUNNING`, `SUCCESS`, `FAILED`, or `CANCELLED`.
- `triggerType`: `SCHEDULED`, `MANUAL`, or `SYSTEM`.
- `startedAt`: start timestamp.
- `finishedAt`: optional finish timestamp.
- `durationMillis`: optional duration.
- `errorMessage`: optional error summary.

Execution records intentionally store task snapshots so history remains readable after a task definition changes.

## API Design

Base path: `/api`.

Task APIs:

- `GET /api/tasks`: list task definitions.
- `POST /api/tasks`: create a task definition.
- `GET /api/tasks/{code}`: get one task definition.
- `PUT /api/tasks/{code}`: update one task definition.
- `POST /api/tasks/{code}/enable`: enable and schedule a task.
- `POST /api/tasks/{code}/disable`: disable and unschedule a task.
- `POST /api/tasks/{code}/pause`: pause a scheduled task.
- `POST /api/tasks/{code}/resume`: resume a paused task.
- `POST /api/tasks/{code}/trigger`: manually trigger a task.
- `DELETE /api/tasks/{code}`: delete a task definition and unschedule it.

Execution APIs:

- `GET /api/executions`: list recent execution records.
- `GET /api/tasks/{code}/executions`: list execution records for one task.

Error responses:

- Validation failures return HTTP 400 with field-level messages.
- Missing resources return HTTP 404.
- Conflicts such as duplicate task code return HTTP 409.
- Unexpected errors return HTTP 500 with a generic message.

## Admin Console

The first page shown after login is the dashboard.

Pages:

- `/login`: form login page.
- `/admin`: dashboard with task and execution summary.
- `/admin/tasks`: task table with code, name, status, priority, cron, rate limit, and operations.
- `/admin/executions`: execution history table with task, status, trigger type, time, duration, and error summary.
- `/swagger-ui.html`: API documentation and manual API testing.

The UI should be operational and compact. It should look like a management tool, not a marketing site. The initial pages use server-rendered forms and links; asynchronous interactions are outside this first implementation scope.

## Scheduling Behavior

Quartz is the scheduling engine. Task definitions with `ENABLED` status are registered with Quartz during application startup and whenever a task is enabled or updated.

Initial execution behavior is intentionally simple:

- Manual trigger creates an execution record and invokes a built-in local no-op handler.
- Scheduled trigger uses a Quartz job that records execution start and finish.
- The built-in local no-op handler completes successfully by default.
- Handler dispatch is represented by `handlerName` so future implementations can route to real Java handlers, HTTP callbacks, or external workers.

This gives the platform a runnable scheduling loop while keeping business execution pluggable.

## Security

Spring Security protects admin pages and management APIs.

Initial local credentials:

- username: `admin`
- password: configured through `app.security.admin-password`, defaulting to `admin123` for local development only.

H2 console is enabled for local development and can be disabled by changing the active Spring profile.

## Testing Strategy

Tests should verify the scaffold behaves as a platform, not only that the application starts.

Required coverage:

- Application context loads.
- Admin routes require authentication.
- Swagger/OpenAPI route is available.
- Task API can create, list, enable, pause, resume, trigger, and delete tasks.
- Duplicate task codes produce conflict responses.
- Invalid task input produces validation responses.
- Repository persists task definitions and execution records.
- Scheduler service calls can register and unschedule jobs without controller involvement.

## Acceptance Criteria

- `mvn test` passes.
- `mvn spring-boot:run` starts the application on port `8080`.
- Login works with the configured local admin user.
- `/admin` shows a dashboard.
- `/admin/tasks` shows task definitions.
- `/admin/executions` shows execution records.
- Swagger UI is reachable.
- REST APIs support task CRUD and lifecycle operations.
- H2-backed local data survives while the app process runs.
- The codebase has clear package boundaries for future scheduler expansion.

## References

- Spring Boot official system requirements: `https://docs.spring.io/spring-boot/system-requirements.html`
- springdoc-openapi getting started: `https://springdoc.org/getting-started.html`
