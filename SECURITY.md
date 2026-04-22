# SECURITY REVIEW — Risk Treatment Planner

## Reviewer
Harsha Vardhana S

## Scope
- Authentication (JWT)
- Authorization (RBAC)
- Input validation
- AI security
- Rate limiting
- OWASP Top 10

## Planned Tests

### 1. Authentication
- Access API without token → expect 401

### 2. Authorization
- Invalid role access → expect 403

### 3. Input Validation
Test:
- <script>alert(1)</script>
- ' OR 1=1 --

Expect:
- 400 Bad Request

### 4. Rate Limiting
- >30 requests/min → expect 429

### 5. AI Security
- Prompt injection attempts → should be blocked

## Status
Initial setup completed. Waiting for APIs.