# System test report — Docker Compose & end-to-end features

**Date:** 2026-05-03  

## What actually ran in this environment

| Step | Result |
|------|--------|
| `docker compose down -v` then `docker compose up --build -d` | **Not executed in CI/agent:** `docker` may be absent from `PATH`. **Use `scripts/compose-verify.ps1`** on your PC (with Docker installed): it runs compose tear-down/rebuild, waits for `/v3/api-docs`, prints Swagger URL or logs on failure. |
| Full automated regression | **`mvn test`** should pass after changes (JWT now reads `jwt.secret` / `JWT_SECRET`). |

**Action for you:** With Docker Desktop running:

```powershell
.\scripts\compose-verify.ps1
```

Or manually: `docker compose down -v`, `docker compose up --build -d`, then open `http://localhost:${BACKEND_PORT:-8080}/swagger-ui/index.html`.

---

## Intended feature walkthrough (what “full E2E” should cover)

1. **Infrastructure:** Postgres, Redis, PgAdmin, backend JAR, nginx “frontend” placeholder — all healthy per `depends_on` / healthchecks.
2. **Auth:** `POST /auth/register` → `POST /auth/login` → JWT in responses → optional `POST /auth/refresh` with `Authorization: Bearer …`.
3. **Risks (JWT):** `GET /api/risks/all`, `GET /api/risks/{id}`, `POST /api/risks/create`, `PUT /api/risks/{id}`, `DELETE /api/risks/{id}`, `GET /api/risks/export`.
4. **Files (JWT):** `POST /upload` (multipart), `GET /files/{id}` download by stored UUID prefix.
5. **Docs:** OpenAPI at `/v3/api-docs`, Swagger UI as above.
6. **Background:** Scheduled jobs + “email” logging (stdout); audit logging on risk mutations via AOP.

---

## Bug & gap register

Severity is **impact on prod / compose E2E**, not style.

### BLOCKER — Docker Compose stack may never become healthy (backend)

**Symptom:** `risk-backend` healthcheck runs `wget` against `http://localhost:8080/v3/api-docs`. The runtime image is `eclipse-temurin:17-jre-alpine`, which typically **does not ship `wget`**. If the check fails, the container stays **unhealthy**, and **`risk-frontend` never starts** (`depends_on: condition: service_healthy`).

**Evidence:** `docker-compose.yml` lines 76–80; `Dockerfile` run stage is minimal Alpine JRE.

**Suggested fix:** Install `wget`/`curl` in the image, or change the healthcheck to `CMD-SHELL` with a tool that exists (e.g. add `apk add --no-cache wget`), or use Spring Boot’s Actuator health endpoint with a tiny HTTP client baked in.

---

### CRITICAL — No supported path to ADMIN / MANAGER after fresh DB

**Symptom:** New users registered via `POST /auth/register` get role **`VIEWER`** only (`AuthController`). No Flyway seed inserts an admin. Therefore **`POST /api/risks/create`**, **`PUT /api/risks/{id}`**, and **`DELETE /api/risks/{id}`** return **403** for every self-registered user.

**Evidence:** `AuthController` sets `user.setRole("VIEWER")`; migrations create tables only (no seed users); `@PreAuthorize` on `RiskController` requires `ADMIN`/`MANAGER`.

**Impact:** Out-of-the-box Docker deployment cannot exercise risk write APIs without manual DB edits.

---

### HIGH — Token refresh drops role to VIEWER

**Symptom:** `POST /auth/refresh` issues a new JWT with `jwtUtil.generateToken(username, "VIEWER")`, ignoring the role embedded in the original token / DB.

**Evidence:** `AuthController.refresh`.

**Impact:** Any future elevation to ADMIN/MANAGER would be lost on refresh (and documentation implies refresh without noting this).

---

### HIGH — Risk update ignores most fields

**Symptom:** `RiskService.update` copies only **title, description, status**. **Category, likelihood, impact, dueDate, aiDescription** sent in JSON are ignored.

**Evidence:** `RiskService.update`.

**Impact:** Clients think they updated a risk; fields silently unchanged.

---

### HIGH — “Soft delete” columns vs hard delete

**Symptom:** Table and entity have **`is_deleted` / `deleted_at`**, but `RiskService.delete` calls **`riskRepository.deleteById`** (physical delete). Queries like `findByDeletedFalse` become inconsistent with deletes performed via API.

**Evidence:** `RiskService.delete`; `V1__create_core_table.sql` / `Risk` entity.

---

### HIGH — Upload storage not persisted in Compose

**Symptom:** `file.upload-dir` defaults to `uploads` **inside the container filesystem**. `docker-compose.yml` defines **no volume** for `risk-backend` uploads. Recreate the container → **uploaded files disappear** (download by id fails).

**Evidence:** `application.properties`; `docker-compose.yml` (no bind/volume for uploads).

---

### MEDIUM — Audit log for UPDATE stores wrong `newValue`

**Symptom:** `AuditAspect.auditUpdate` serializes the **return value of `RiskService.update`**, which is **`Optional<Risk>`**, not the updated entity. The audit trail gets an Optional-shaped JSON blob instead of the risk payload.

**Evidence:** `AuditAspect` around `RiskService.update`; `RiskService.update` return type.

---

### MEDIUM — Audit UPDATE `oldValue` is not the previous entity state

**Symptom:** `oldValue` is set to the literal `"id=" + id`, not a snapshot of the row before update.

**Evidence:** `AuditAspect.auditUpdate`.

---

### MEDIUM — Overdue reminder passes title twice

**Symptom:** `sendOverdueAlert(risk.getTitle(), risk.getTitle())` — parameter named **owner** in `EmailService` receives **title** twice; no owner field on `Risk`.

**Evidence:** `RiskReminderScheduler.sendOverdueReminders`; `EmailService.sendOverdueAlert`.

---

### MEDIUM — Production noise: “test” scheduler every 2 minutes

**Symptom:** `RiskReminderScheduler.testScheduler` runs **`fixedRate = 120000`** (every 2 minutes) forever, including in Docker/prod, spamming logs and querying overdue risks.

**Evidence:** `RiskReminderScheduler`.

---

### MEDIUM — Email “feature” is stdout only

**Symptom:** `EmailService` only **prints** messages; no SMTP, no failure handling. Weekly/overdue features are **non-functional** from a user perspective.

**Evidence:** `EmailService`.

---

### MEDIUM — Redis service unused by application code

**Symptom:** Compose starts **Redis** and injects `SPRING_DATA_REDIS_HOST`, but there is **no** `RedisTemplate`, cache annotations, or other Redis usage under `src/main/java`.

**Evidence:** `grep` over main sources; `docker-compose.yml` still provisions Redis.

**Impact:** Extra moving part with no feature benefit; operational cost only.

---

### LOW — Invalid JPQL on title search (if ever called)

**Symptom:** `RiskRepository.searchByTitle` uses `LIKE %:keyword%`, which is **invalid JPQL** (should use `CONCAT` or bind a pattern).

**Evidence:** `RiskRepository`.

---

### LOW — `RiskRepository.countByStatusAndDeletedFalse("OPEN")` vs mixed casing

**Symptom:** Weekly summary counts status **`OPEN`** (uppercase). UI/other paths might send **`Open`**. PostgreSQL string compare is case-sensitive → **under-counting** if statuses vary.

**Evidence:** `RiskService.countOpenRisks`; scheduler uses this.

---

### LOW — JWT validation errors only logged to stdout

**Symptom:** `JwtFilter` catches exceptions from parsing JWT and prints **`JWT Error:`**; request continues **without** authentication → downstream returns **401**, but errors are not structured logs and may leak to console in prod.

**Evidence:** `JwtFilter`.

---

### DOCUMENTATION / PRODUCT — “Frontend” service

**Symptom:** `risk-frontend` is **stock nginx**, not the product UI. Only the default welcome page is expected.

**Evidence:** `docker-compose.yml` (`image: nginx:alpine`, no custom build context).

---

### CONFIG — Hibernate dialect warnings

**Symptom:** Logs recommend removing explicit `PostgreSQLDialect` and show version warnings. Non-blocking but noisy in tests and runtime.

**Evidence:** Test output during `mvn test`; `application.properties`.

---

### CONFIG — Flyway + `ddl-auto=update`

**Symptom:** `spring.jpa.hibernate.ddl-auto=update` alongside Flyway can cause **schema drift** between environments.

**Evidence:** `application.properties`.

---

## Summary count

| Severity | Count |
|----------|-------|
| Blocker (compose health / stack start) | 1 |
| Critical (cannot use risk writes on fresh install) | 1 |
| High | 4 |
| Medium | 7 |
| Low / doc / config | 5 |

---

## Re-run checklist (on your machine with Docker)

1. `docker compose down -v` — confirm volumes removed.
2. `docker compose up --build -d` — wait until all services **healthy** (or confirm backend failure mode if healthcheck breaks).
3. Swagger: register → login → authorize → call `GET /api/risks/all` (200) → try `POST /api/risks/create` (expect **403** as VIEWER — confirms critical gap #2).
4. Upload a small file → download by returned UUID → restart backend container → confirm whether download still works (confirms upload persistence bug if no volume).
