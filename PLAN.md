# Wheel of Fortune — Meeting Picker
### Claude Code Session Kickoff Document

---

## Project Summary

A full stack web application that lets a logged-in user maintain a personal list of coworkers and spin a colorful, animated wheel to randomly select one for a monthly meeting role. Each user's list is private. The app also tracks spin history so users can see who has been picked and when.

---

## Tech Stack

| Layer | Choice | Notes |
|---|---|---|
| Frontend | React (Vite) | Component-based, good ecosystem for canvas/SVG animation |
| Backend | Spring Boot | REST API, familiar for Java-based QE work |
| Database | H2 (in-memory) | Zero setup friction; swap to Postgres later if needed |
| Auth | JWT | Stateless, industry standard; BCrypt for password hashing |
| FE Testing | React Testing Library + Vitest | RTL for component tests |
| BE Testing | JUnit 5 + Mockito | Standard Spring Boot testing stack |
| CI | GitHub Actions | Workflow to run both test suites on push |

---

## Data Model

### `users`
| Field | Type | Notes |
|---|---|---|
| id | Long | PK, auto-increment |
| username | String | Unique |
| password | String | BCrypt hashed — never plaintext |

### `participants`
| Field | Type | Notes |
|---|---|---|
| id | Long | PK |
| name | String | Display name on the wheel |
| active | Boolean | Whether they appear on the wheel |
| user_id | Long | FK → users; each participant belongs to one user |

### `spin_history`
| Field | Type | Notes |
|---|---|---|
| id | Long | PK |
| picked_name | String | Name of the person selected |
| spun_at | Timestamp | When the spin occurred |
| user_id | Long | FK → users |

---

## API Endpoints

### Auth
- `POST /api/auth/register` — create account
- `POST /api/auth/login` — returns JWT

### Participants
- `GET /api/participants` — get all participants for logged-in user
- `POST /api/participants` — add a new participant
- `PATCH /api/participants/{id}` — toggle active/inactive
- `DELETE /api/participants/{id}` — remove participant

### Spin History
- `POST /api/spins` — record a spin result
- `GET /api/spins` — get spin history for logged-in user

> All `/api/participants` and `/api/spins` routes require a valid JWT in the `Authorization: Bearer <token>` header.

---

## Frontend Pages & Components

### Pages
- **Login / Register** — simple tabbed auth form
- **Main App** — authenticated view; wheel + participant list side by side

### Key Components
- `Wheel` — SVG-based, dynamically divides into equal wedges based on active participant count. Each wedge is a distinct color from a predefined palette. Animates a spin (CSS rotation with ease-out) and lands on a randomly selected person.
- `ParticipantList` — scrollable list below or beside the wheel. Each entry has a toggle (active/inactive) and a delete button. Includes an "Add Person" input.
- `SpinHistory` — collapsible panel showing the last N spins with name and date.

### Wheel Behavior
- Only `active` participants appear as wedges.
- Wedge size = `360 / activeCount` degrees each — always equal.
- Color palette cycles through at least 8 distinct, accessible colors.
- On spin: wheel rotates a random number of full rotations + offset to land on the winner. Winner is determined in JS first, then the rotation is calculated to match.
- After spin settles: a result banner displays the winner's name and a "Record Spin" button saves it to history.

---

## Core Requirements Checklist

### Authentication
- [x] User registration with hashed password (BCrypt)
- [x] User login returning JWT
- [x] All data routes protected by JWT middleware
- [x] Passwords never stored in plaintext

### Core Features
- [x] Add / remove participants
- [x] Toggle participants active/inactive (affects wheel)
- [x] Spin the wheel — animated, lands on random active participant
- [x] Record and view spin history

### Testing & Quality
- [x] Backend unit test coverage ≥ 70% (JUnit + Mockito)
- [x] Frontend unit test coverage ≥ 60% (RTL + Vitest)
- [x] Unit tests only (no integration or E2E)
- [x] GitHub Actions workflow runs tests on push
- [x] Clean, readable code — no over-engineering

---

## Deliverables

1. Source code in a local Git repository
2. `README.md` with setup instructions and how to run locally
3. Unit tests for backend (JUnit) and frontend (RTL)
4. Test coverage reports (≥70% BE, ≥60% FE)
5. Working app running on `localhost`

---

## Key Principles

- **Quality over quantity** — a polished small app beats a bloated incomplete one
- **No over-engineering** — no microservices, no external DBs, no deployment infra needed
- **Functional UI** — clean and usable; the wheel animation is the one visual flourish
- **All auth in place** — even for a simple app, treat security seriously (hashed passwords, JWT expiry)

---

## Suggested Project Structure

```
/
├── backend/                  # Spring Boot project
│   ├── src/main/java/...
│   │   ├── controller/       # Auth, Participant, Spin controllers
│   │   ├── service/          # Business logic
│   │   ├── repository/       # JPA repositories
│   │   ├── model/            # User, Participant, SpinHistory entities
│   │   ├── security/         # JWT filter, config
│   │   └── dto/              # Request/response DTOs
│   └── src/test/java/...
│
├── frontend/                 # React + Vite project
│   ├── src/
│   │   ├── components/       # Wheel, ParticipantList, SpinHistory
│   │   ├── pages/            # LoginPage, MainPage
│   │   ├── api/              # Axios API calls
│   │   └── context/          # AuthContext (JWT storage)
│   └── src/__tests__/
│
└── .github/
    └── workflows/
        └── ci.yml            # Run BE + FE tests on push
```

---

## Notes for Claude Code

- Start with the backend: get auth working first (register + login + JWT filter), then add participant CRUD, then spin history.
- Use `@WithMockUser` and Mockito for backend unit tests — avoid spinning up the full application context where possible.
- For the wheel, build it as a pure SVG component that accepts an array of names as props. Keep the spin logic (random selection + rotation math) in a separate utility function so it's easy to unit test.
- The `active` flag on participants is the key piece of state — the wheel should re-render reactively whenever the list changes.
- H2 console can be enabled during development at `/h2-console` for easy DB inspection.