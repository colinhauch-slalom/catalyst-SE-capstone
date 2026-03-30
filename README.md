# Wheel of Doom — Meeting Picker

A full-stack web app to spin a wheel and randomly pick a coworker for a monthly meeting role. Each user maintains their own private participant list, and spin history is recorded per user.

## Current Status

- Core flow is working end to end: register, log in, manage a private participant list, spin the wheel, and view saved spin history.
- Backend is running on Spring Boot 3 with JWT auth and an H2 in-memory database.
- Frontend is running on React 18 + Vite with authenticated API calls proxied to the backend.
- Participant active/inactive changes are edited in the UI and then persisted with an explicit Save changes action.
- Current verified test status:
	- Backend: `mvn test` passes with 28 tests.
	- Frontend: `npm test -- --run` passes with 27 tests across 9 test files.
- Current gaps: backend integration coverage is still lighter than unit coverage, and frontend tests still emit some `act(...)` warning noise during auth-page runs.

## Tech Stack

- **Backend**: Spring Boot 3 (Java 21), H2 in-memory database, JWT auth
- **Frontend**: React 18 (Vite), React Router, Axios, Vitest + React Testing Library
- **CI**: GitHub Actions

## Prerequisites

| Tool | Version |
|---|---|
| Java | 21 |
| Maven | 3.9+ |
| Node.js | 20+ |
| npm | 10+ |

## Running Locally

### Full stack from repo root

Install root dev dependencies once:

```bash
npm install
```

Run backend and frontend together:

```bash
npm run dev
```

This starts:

- Backend on **http://localhost:8080**
- Frontend on **http://localhost:5173**

### Backend

```bash
cd backend
mvn spring-boot:run
```

The API starts on **http://localhost:8080**.  
H2 console available at **http://localhost:8080/h2-console** (JDBC URL: `jdbc:h2:mem:wheelofdoom`).

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The app starts on **http://localhost:5173**.  
API calls to `/api/*` are proxied to `http://localhost:8080` via Vite's dev proxy.

## Running Tests

### From repo root

```bash
npm --prefix frontend test -- --run
mvn -f backend/pom.xml test
```

### Backend

```bash
cd backend
mvn test
```

Run only the password-encryption persistence validation:

```bash
cd backend
mvn -Dtest=AuthPersistenceIntegrationTest test
```

Generate and view the HTML coverage report:

```bash
cd backend
mvn test
open target/site/jacoco/index.html
```

From the repo root, you can also use:

```bash
mvn -f backend/pom.xml test
open backend/target/site/jacoco/index.html
```

Backend JaCoCo report output:

- HTML: `backend/target/site/jacoco/index.html`
- CSV: `backend/target/site/jacoco/jacoco.csv`
- XML: `backend/target/site/jacoco/jacoco.xml`

### Password Encryption Validation

The backend includes an integration test that verifies passwords are encrypted before they are stored:

- `AuthPersistenceIntegrationTest` registers a user through the real `AuthService` and confirms the saved password is not plaintext.
- The same test confirms the persisted value matches the configured `PasswordEncoder` and has the expected BCrypt-style hash prefix.
- It also verifies the seeded `demo` account is stored encrypted, not as the raw string `demo`.

If you want to inspect the stored values manually while the backend is running, open the H2 console and run:

```sql
SELECT username, password FROM users;
```

You should see hashed values beginning with something like `$2`, not readable passwords.

### Frontend

```bash
cd frontend
npm test -- --run          # single run
npm run coverage           # with coverage report
```

## GitHub Actions Workflows

Example GitHub Actions workflows live in `.github/workflows`:

- `ci.yml`: runs backend Maven tests and frontend Vitest tests on `push` and `pull_request`.
- `build-check.yml`: runs backend `mvn verify` and frontend `npm run build` on `pull_request` and manual dispatch.
- `coverage.yml`: generates backend and frontend coverage reports and uploads them as workflow artifacts.
- `dependency-audit.yml`: runs a scheduled frontend dependency audit and backend dependency snapshot, and can also be started manually.

### Viewing Workflow Results

After pushing the branch to GitHub:

1. Open the repository on GitHub.
2. Click the **Actions** tab.
3. Select a workflow run to inspect job and step logs.
4. For the coverage workflow, download the uploaded artifacts from the run summary page.

Manual workflows can be started from the same **Actions** tab with **Run workflow**.

## API Overview

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | No | Create account |
| POST | `/api/auth/login` | No | Returns JWT |
| GET | `/api/participants` | Yes | List participants |
| POST | `/api/participants` | Yes | Add participant |
| POST | `/api/participants/{id}/active` | Yes | Update active status |
| DELETE | `/api/participants/{id}` | Yes | Remove participant |
| POST | `/api/spins` | Yes | Record spin result |
| GET | `/api/spins` | Yes | Get spin history |

Protected routes require `Authorization: Bearer <token>` header.
