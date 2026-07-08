# Task Scheduling Platform Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a runnable Java 21 Spring Boot task scheduling platform with Swagger, Security, JPA, Quartz, and a Thymeleaf admin console.

**Architecture:** Use a Maven Spring Boot single service with package boundaries for config, task domain/application/web, execution domain/application, and admin pages. Keep controllers thin, put transactions in application services, and use Quartz through a scheduler coordination service.

**Tech Stack:** Java 21, Spring Boot 4.1.x, Maven, Spring MVC, Thymeleaf, Spring Security, Spring Data JPA, H2, Quartz, Actuator, springdoc-openapi, JUnit, MockMvc.

---

## File Structure

- Create `pom.xml`: Maven build, Java 21, Spring Boot dependencies.
- Create `src/main/java/com/fox/taskscheduling/FoxTaskSchedulingApplication.java`: application entry point.
- Create `src/main/resources/application.yml`: local configuration, H2, Quartz, admin password, OpenAPI.
- Create `src/main/java/com/fox/taskscheduling/config/*`: Security, OpenAPI, data seeding.
- Create `src/main/java/com/fox/taskscheduling/task/domain/*`: task entity, status enum, repository.
- Create `src/main/java/com/fox/taskscheduling/task/application/*`: task service and Quartz scheduler coordinator.
- Create `src/main/java/com/fox/taskscheduling/task/web/api/*`: task REST controller and DTOs.
- Create `src/main/java/com/fox/taskscheduling/task/web/admin/*`: admin page controllers.
- Create `src/main/java/com/fox/taskscheduling/execution/domain/*`: execution entity, status and trigger enums, repository.
- Create `src/main/java/com/fox/taskscheduling/execution/application/*`: execution recording service.
- Create `src/main/java/com/fox/taskscheduling/common/web/*`: API error response and exception handler.
- Create `src/main/resources/templates/*`: login, layout fragments, dashboard, tasks, executions.
- Create `src/main/resources/static/css/admin.css`: compact admin styling.
- Create `src/test/java/com/fox/taskscheduling/*`: context, API, service, repository, and security/page tests.

---

### Task 1: Maven Bootstrapping

**Files:**
- Create: `pom.xml`
- Create: `src/main/java/com/fox/taskscheduling/FoxTaskSchedulingApplication.java`
- Create: `src/main/resources/application.yml`
- Test: `src/test/java/com/fox/taskscheduling/FoxTaskSchedulingApplicationTests.java`

- [ ] **Step 1: Write failing context test**

```java
package com.fox.taskscheduling;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FoxTaskSchedulingApplicationTests {

    @Test
    void contextLoads() {
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -q -Dtest=FoxTaskSchedulingApplicationTests test`

Expected: FAIL because `pom.xml` and the application class do not exist yet.

- [ ] **Step 3: Add Maven project and application bootstrap**

Implement `pom.xml` with Spring Boot parent, Java 21, and dependencies for web, thymeleaf, security, validation, data-jpa, h2, quartz, actuator, springdoc-openapi, and tests. Add `FoxTaskSchedulingApplication` with `SpringApplication.run(...)`. Add `application.yml` for H2, JPA, Quartz JDBC store disabled for first local run, admin password, and actuator exposure.

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn -q -Dtest=FoxTaskSchedulingApplicationTests test`

Expected: PASS.

---

### Task 2: Domain Persistence

**Files:**
- Create: `src/main/java/com/fox/taskscheduling/task/domain/TaskDefinition.java`
- Create: `src/main/java/com/fox/taskscheduling/task/domain/TaskStatus.java`
- Create: `src/main/java/com/fox/taskscheduling/task/domain/TaskDefinitionRepository.java`
- Create: `src/main/java/com/fox/taskscheduling/execution/domain/TaskExecutionRecord.java`
- Create: `src/main/java/com/fox/taskscheduling/execution/domain/ExecutionStatus.java`
- Create: `src/main/java/com/fox/taskscheduling/execution/domain/TriggerType.java`
- Create: `src/main/java/com/fox/taskscheduling/execution/domain/TaskExecutionRecordRepository.java`
- Test: `src/test/java/com/fox/taskscheduling/task/domain/TaskDefinitionRepositoryTests.java`

- [ ] **Step 1: Write failing repository test**

Test saving a task with code `sample.cleanup`, status `ENABLED`, cron `0 0/5 * * * ?`, and priority `10`, then loading by code.

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -q -Dtest=TaskDefinitionRepositoryTests test`

Expected: FAIL because entities and repositories do not exist.

- [ ] **Step 3: Implement entities and repositories**

Use Jakarta Persistence annotations, unique constraint on `TaskDefinition.code`, and repository methods `findByCode`, `existsByCode`, `deleteByCode`, `findTop50ByOrderByStartedAtDesc`, and `findTop50ByTaskCodeOrderByStartedAtDesc`.

- [ ] **Step 4: Run repository test**

Run: `mvn -q -Dtest=TaskDefinitionRepositoryTests test`

Expected: PASS.

---

### Task 3: Application Services and Scheduling

**Files:**
- Create: `src/main/java/com/fox/taskscheduling/task/application/TaskDefinitionService.java`
- Create: `src/main/java/com/fox/taskscheduling/task/application/TaskSchedulerService.java`
- Create: `src/main/java/com/fox/taskscheduling/task/application/ScheduledTaskJob.java`
- Create: `src/main/java/com/fox/taskscheduling/task/application/dto/*`
- Create: `src/main/java/com/fox/taskscheduling/execution/application/TaskExecutionService.java`
- Create: `src/main/java/com/fox/taskscheduling/common/exception/*`
- Test: `src/test/java/com/fox/taskscheduling/task/application/TaskDefinitionServiceTests.java`

- [ ] **Step 1: Write failing service tests**

Test create, duplicate code conflict, enable, pause, resume, disable, delete, and manual trigger workflows using real repositories and a mocked Quartz `Scheduler`.

- [ ] **Step 2: Run tests to verify they fail**

Run: `mvn -q -Dtest=TaskDefinitionServiceTests test`

Expected: FAIL because services do not exist.

- [ ] **Step 3: Implement services**

Add transactional task service methods for list, get, create, update, enable, disable, pause, resume, trigger, and delete. Add scheduler coordinator methods to schedule, unschedule, pause, resume, and trigger Quartz jobs by task code. Add execution service methods to start, complete, fail, and list records.

- [ ] **Step 4: Run service tests**

Run: `mvn -q -Dtest=TaskDefinitionServiceTests test`

Expected: PASS.

---

### Task 4: REST API and Error Handling

**Files:**
- Create: `src/main/java/com/fox/taskscheduling/task/web/api/TaskApiController.java`
- Create: `src/main/java/com/fox/taskscheduling/task/web/api/ExecutionApiController.java`
- Create: `src/main/java/com/fox/taskscheduling/task/web/api/dto/*`
- Create: `src/main/java/com/fox/taskscheduling/common/web/ApiErrorResponse.java`
- Create: `src/main/java/com/fox/taskscheduling/common/web/GlobalExceptionHandler.java`
- Create: `src/main/java/com/fox/taskscheduling/config/OpenApiConfig.java`
- Test: `src/test/java/com/fox/taskscheduling/task/web/api/TaskApiControllerTests.java`

- [ ] **Step 1: Write failing API tests**

Test authenticated API create/list/enable/pause/resume/trigger/delete, duplicate code returns 409, invalid input returns 400, and missing task returns 404.

- [ ] **Step 2: Run API tests to verify they fail**

Run: `mvn -q -Dtest=TaskApiControllerTests test`

Expected: FAIL because controllers, DTOs, security, and handler do not exist.

- [ ] **Step 3: Implement API layer**

Create request DTOs with validation annotations. Create response DTOs. Implement REST controllers under `/api/tasks` and `/api/executions`. Implement global exception mapping.

- [ ] **Step 4: Run API tests**

Run: `mvn -q -Dtest=TaskApiControllerTests test`

Expected: PASS.

---

### Task 5: Security, Admin UI, and Seed Data

**Files:**
- Create: `src/main/java/com/fox/taskscheduling/config/SecurityConfig.java`
- Create: `src/main/java/com/fox/taskscheduling/config/DataInitializer.java`
- Create: `src/main/java/com/fox/taskscheduling/task/web/admin/AdminPageController.java`
- Create: `src/main/resources/templates/login.html`
- Create: `src/main/resources/templates/admin/dashboard.html`
- Create: `src/main/resources/templates/admin/tasks.html`
- Create: `src/main/resources/templates/admin/executions.html`
- Create: `src/main/resources/static/css/admin.css`
- Test: `src/test/java/com/fox/taskscheduling/task/web/admin/AdminPageControllerTests.java`

- [ ] **Step 1: Write failing admin tests**

Test `/admin` redirects unauthenticated users to login, authenticated users can see dashboard, `/admin/tasks` shows seeded task data, and Swagger route is not blocked after authentication.

- [ ] **Step 2: Run admin tests to verify they fail**

Run: `mvn -q -Dtest=AdminPageControllerTests test`

Expected: FAIL because security and templates do not exist.

- [ ] **Step 3: Implement security, admin pages, and seed data**

Use form login at `/login`, default admin user from `app.security.admin-password`, BCrypt password encoder, and CSRF disabled for API simplicity in the first scaffold. Add compact Thymeleaf pages and CSS. Seed sample tasks and one execution record.

- [ ] **Step 4: Run admin tests**

Run: `mvn -q -Dtest=AdminPageControllerTests test`

Expected: PASS.

---

### Task 6: Full Verification

**Files:**
- Modify: `README.md`

- [ ] **Step 1: Update README**

Document Java 21, Maven commands, local login credentials, Swagger URL, H2 console URL, and main admin routes.

- [ ] **Step 2: Run all tests**

Run: `mvn test`

Expected: BUILD SUCCESS.

- [ ] **Step 3: Run package build**

Run: `mvn -q -DskipTests package`

Expected: jar is created under `target/`.

- [ ] **Step 4: Inspect Git status**

Run: `git status --short`

Expected: only intended scaffold files are modified or added.
