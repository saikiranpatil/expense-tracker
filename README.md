# 💰 Expense Tracker

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Python](https://img.shields.io/badge/Python-3.11-blue?logo=python)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=springboot)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-7.5-black?logo=apachekafka)
![Kong](https://img.shields.io/badge/Kong%20Gateway-3.8-blue?logo=kong)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)
![License](https://img.shields.io/badge/License-TODO-lightgrey)

> An AI-powered, event-driven expense tracking platform built with a microservices architecture — automatically extract and categorize expenses from bank SMS messages using an LLM.

---

## Overview

**Expense Tracker** is a cloud-native, microservices-based application that allows users to register, authenticate, and manage their financial expenses. Its standout feature is the **Data Service** — a Python/Flask microservice that uses the **Mistral AI LLM** (via LangChain) to parse natural-language bank SMS messages and automatically extract structured expense data (amount, payee, date, category, etc.), which is then persisted asynchronously via Kafka.

All inter-service communication is routed through a **Kong API Gateway** equipped with a custom Lua plugin (`auth-validator`) that enforces JWT-based authentication on every protected route. Services communicate asynchronously over **Apache Kafka** topics, and persistent storage is handled by **MySQL**.

---

## Features

- 🔐 **JWT Authentication** — Secure login, sign-up, and token refresh with access & refresh token support
- 🤖 **AI-Powered SMS Parsing** — Send a raw bank SMS and receive a structured expense object back (amount, payee, category, currency, and more)
- 📨 **Event-Driven Architecture** — Kafka topics decouple services; expense data flows from the data service to the expense service automatically
- 🛡️ **API Gateway with Custom Auth Plugin** — Kong Gateway with a custom Lua `auth-validator` plugin validates JWTs on every protected endpoint before forwarding requests
- 👤 **User Profile Management** — Create and update user profiles synced via Kafka events from the auth service
- 💸 **Expense Management** — Add, retrieve, and update expenses per user with full CRUD support
- 🐳 **Fully Dockerized** — Every service ships with a `Dockerfile`; the entire stack runs with a single `docker-compose` command

---

## Tech Stack

| Layer | Technology |
|---|---|
| API Gateway | Kong Gateway 3.8 (DB-less mode) + custom Lua plugin |
| Auth Service | Java 21, Spring Boot 3, Spring Security, JWT (JJWT) |
| User Service | Java 21, Spring Boot 3, Spring Data JPA |
| Expense Service | Java 21, Spring Boot 3, Spring Data JPA, MapStruct |
| Data Service | Python 3.11, Flask, LangChain, Mistral AI, Pydantic |
| Message Broker | Apache Kafka (KRaft mode, Confluent 7.5) |
| Database | MySQL 8.3 |
| Build Tools | Gradle 8.12 (Java services), pip (Python service) |
| Containerization | Docker, Docker Compose |

---

## Architecture

```
Client
  │
  ▼
Kong API Gateway (:8000)
  │  (auth-validator plugin calls auth-service /ping on protected routes)
  ├──▶ Auth Service    (:9898)  ──Kafka──▶ User Service  (:9899)
  ├──▶ User Service    (:9899)
  ├──▶ Expense Service (:9880)  ◀──Kafka── Data Service  (:9881)
  └──▶ Data Service    (:9881)
            │
            └──▶ Mistral AI API (LLM)
```

---

## Installation

### Prerequisites

| Tool | Version |
|---|---|
| Docker | 24+ |
| Docker Compose | v2+ |
| Java (for local dev) | 21+ |
| Python (for local dev) | 3.11+ |
| Mistral AI API Key | [Get one here](https://console.mistral.ai/) |

### 1. Clone the repository

```bash
git clone <REPO_URL>
cd expense-tracker
```

### 2. Set up environment variables

Create a `.env` file in the project root:

```env
# Required for the data-service LLM integration
MISTRALAI_KEY=your_mistral_api_key_here
```

### 3. Build service Docker images

Build each Java service from its directory:

```bash
# Auth Service
cd services/auth-service
./gradlew build
docker build -t auth-service .

# User Service
cd ../user-service
./gradlew build
docker build -t user-service .

# Expense Service
cd ../expense-service
./gradlew build
docker build -t expense-service .

# Data Service
cd ../data-service
docker build -t data-service .
```

### 4. Start the full stack

```bash
cd deps
docker-compose up -d
```

This starts Kafka, MySQL, all four microservices, and Kong Gateway.

### 5. Verify services are running

```bash
# Check Kong proxy
curl http://localhost:8000/auth/v1/health

# Check Kong Admin GUI
open http://localhost:8002
```

---

## Usage

### Sign Up

```bash
curl -X POST http://localhost:8000/auth/v1/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "secret123",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "phoneNumber": 9876543210
  }'
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "token": "550e8400-e29b-41d4-a716-446655440000"
}
```

### Login

```bash
curl -X POST http://localhost:8000/auth/v1/login \
  -H "Content-Type: application/json" \
  -d '{"username": "john_doe", "password": "secret123"}'
```

### Parse an Expense from SMS (AI-powered)

```bash
curl -X POST http://localhost:8000/ds/v1/message \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_access_token>" \
  -d '{"message": "Dear User A/C X8989 debited by Rs 45.99 at MTR Hotel on 2025-02-18 at 2:23 PM."}'
```

**Response:**
```json
{
  "amount": 45.99,
  "payee": "MTR Hotel",
  "account": 8989,
  "txnType": "debit",
  "date": "2025-02-18",
  "category": "food",
  "currency": "INR"
}
```

The parsed expense is automatically published to Kafka and persisted by the Expense Service.

### Get Expenses

```bash
curl http://localhost:8000/expense/v1/getExpenses?X-User-Id=<user_id> \
  -H "Authorization: Bearer <your_access_token>"
```

### Add an Expense Manually

```bash
curl -X POST http://localhost:8000/expense/v1/addExpense \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_access_token>" \
  -d '{
    "amount": 1200,
    "currency": "INR",
    "payee": "Swiggy",
    "category": "food",
    "txnType": "debit"
  }'
```

---

## Project Structure

```
expense-tracker/
├── .env                          # Root env file (MISTRALAI_KEY, etc.)
├── deps/
│   ├── docker-compose.yml        # Full stack orchestration
│   └── config/
│       ├── kong.yml              # Kong declarative config (routes & plugins)
│       └── custom-plugins/
│           └── kong/plugins/
│               └── auth-validator/   # Custom Lua auth plugin
│                   ├── handler.lua
│                   ├── init.lua
│                   └── schema.lua
└── services/
    ├── auth-service/             # Spring Boot — JWT auth, user registration
    │   ├── app/src/main/java/org/example/
    │   │   ├── controller/       # AuthController, TokenController
    │   │   ├── entity/           # UserInfo, UserRole, RefreshToken
    │   │   ├── service/          # JwtService, RefreshTokenService, UserDetailsServiceImpl
    │   │   ├── event/            # Kafka producer & serializer
    │   │   └── Utils/auth/       # JwtAuthFilter, SecurityConfig
    │   └── Dockerfile
    ├── user-service/             # Spring Boot — user profile management
    │   ├── app/src/main/java/org/example/
    │   │   ├── controller/       # UserController
    │   │   ├── service/          # UserService
    │   │   └── event/consumer/   # AuthServiceConsumer (Kafka)
    │   └── Dockerfile
    ├── expense-service/          # Spring Boot — expense CRUD + Kafka consumer
    │   ├── app/src/main/java/org/example/
    │   │   ├── controller/       # ExpenseController
    │   │   ├── service/          # ExpenseService
    │   │   └── event/consumer/   # ExpenseConsumer (Kafka)
    │   └── Dockerfile
    └── data-service/             # Python/Flask — LLM SMS parser
        ├── src/app/
        │   ├── __init__.py       # Flask app & Kafka producer
        │   ├── service/
        │   │   ├── llmService.py     # LangChain + Mistral AI integration
        │   │   └── messageService.py # SMS classification & routing
        │   ├── schema/
        │   │   └── ExpenseSchema.py  # Pydantic output schema
        │   └── utils/
        │       ├── configUtil.py
        │       └── messageUtil.py    # Bank SMS keyword detection
        └── Dockerfile
```

---

## Configuration

### Environment Variables

| Variable | Service | Default | Description |
|---|---|---|---|
| `MYSQL_HOST` | auth, user, expense | `localhost` | MySQL hostname |
| `MYSQL_PORT` | auth, user, expense | `3306` | MySQL port |
| `MYSQL_DB` | auth, user, expense | varies per service | Database name |
| `KAFKA_HOST` | all services | `localhost` | Kafka broker hostname |
| `KAFKA_PORT` | all services | `9092` | Kafka broker port |
| `MISTRALAI_KEY` | data-service | — | **Required.** Mistral AI API key |

### Service Ports

| Service | Internal Port |
|---|---|
| Kong Proxy | 8000 |
| Kong Admin API | 8001 |
| Kong Admin GUI | 8002 |
| Auth Service | 9898 |
| User Service | 9899 |
| Data Service | 9881 |
| Expense Service | 9880 |
| MySQL | 3306 |
| Kafka | 9092 |

### Kafka Topics

| Topic | Producer | Consumer |
|---|---|---|
| `auth-service-topic` | Auth Service | User Service |
| `expense-service-topic` | Data Service | Expense Service |

---

## API Reference

All routes are accessed through Kong at `http://localhost:8000`.

### Auth Service

| Method | Path | Auth Required | Description |
|---|---|---|---|
| `POST` | `/auth/v1/signup` | No | Register a new user |
| `POST` | `/auth/v1/login` | No | Log in and receive tokens |
| `POST` | `/auth/v1/refreshToken` | No | Refresh access token |
| `GET` | `/auth/v1/ping` | Yes (JWT) | Validate token & return userId |
| `GET` | `/auth/v1/health` | No | Health check |

### User Service

| Method | Path | Auth Required | Description |
|---|---|---|---|
| `GET` | `/user/v1/getUser` | Yes | Get current user's profile |
| `POST` | `/user/v1/modify` | Yes | Create or update user profile |
| `GET` | `/user/v1/health` | No | Health check |

### Expense Service

| Method | Path | Auth Required | Description |
|---|---|---|---|
| `GET` | `/expense/v1/getExpenses` | Yes | Get all expenses for the user |
| `POST` | `/expense/v1/addExpense` | Yes | Manually add an expense |
| `GET` | `/expense/v1/health` | No | Health check |

### Data Service

| Method | Path | Auth Required | Description |
|---|---|---|---|
| `POST` | `/ds/v1/message` | Yes | Parse a bank SMS with LLM |
| `GET` | `/ds/v1/health` | No | Health check |

> **Note:** Protected routes require an `Authorization: Bearer <token>` header. Kong's `auth-validator` plugin intercepts the request, validates the JWT against the auth service, and injects `X-User-Id` into the downstream request header.

---

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes with a clear message: `git commit -m "feat: add your feature"`
4. Push to your branch: `git push origin feature/your-feature-name`
5. Open a Pull Request against `main`

Please ensure your code follows existing conventions and that existing functionality is not broken. Adding tests for new features is strongly encouraged.

---

## Roadmap

- [ ] Frontend UI (React or Next.js dashboard)
- [ ] Distributed locking for idempotent expense updates
- [ ] Transactional Kafka consumers with proper idempotency handling in user-service
- [ ] Enable Kafka-based user sync on sign-up
- [ ] Add comprehensive unit and integration tests across all services
- [ ] Add support for multiple currencies and exchange rate conversion
- [ ] Expense analytics and reporting endpoints
- [ ] Kubernetes Helm charts for production deployment
- [ ] Secrets management
- [ ] Refresh token rotation and revocation

---

## Author / Acknowledgements

- **Maintainer:** Saikiran
- **LLM Provider:** [Mistral AI](https://mistral.ai/)
- **API Gateway:** [Kong](https://konghq.com/)
- **Inspired by:** Event-driven microservices patterns with Spring Boot & Kafka
