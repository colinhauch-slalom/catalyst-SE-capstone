# Wheel of Doom — Meeting Picker

A full-stack web app to spin a wheel and randomly pick a coworker for a monthly meeting role. Each user maintains their own private participant list, and spin history is recorded per user.

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

### Backend

```bash
cd backend
mvn test
```

Coverage report generated at `backend/target/site/jacoco/index.html`.

### Frontend

```bash
cd frontend
npm test -- --run          # single run
npm run coverage           # with coverage report
```

## API Overview

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | No | Create account |
| POST | `/api/auth/login` | No | Returns JWT |
| GET | `/api/participants` | Yes | List participants |
| POST | `/api/participants` | Yes | Add participant |
| PATCH | `/api/participants/{id}` | Yes | Toggle active |
| DELETE | `/api/participants/{id}` | Yes | Remove participant |
| POST | `/api/spins` | Yes | Record spin result |
| GET | `/api/spins` | Yes | Get spin history |

Protected routes require `Authorization: Bearer <token>` header.
