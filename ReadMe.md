# Wise‑Style Payments Microservice

A production‑style **fintech backend** inspired by Wise, built with **Java 17 + Spring Boot** and deployed on **Render** with **PostgreSQL**.

This project focuses on **correctness, safety, and real‑world payment patterns** rather than toy CRUD logic.

---

## 🌍 Live Demo

- **Frontend**: [https://payment-system-frontend-17jm.onrender.com ](https://payment-system-frontend-17jm.onrender.com)
- **Frontend Repository**: [https://github.com/chais09/payment-system-frontend](https://github.com/chais09/payment-system-frontend)

---

## 🌍 Live Deployment

**Base URL**

```
https://wise-payments-microservice.onrender.com
```

**Health Check**

```
GET /actuator/health
```

---

## 🚀 Features

### Core Capabilities

* Account creation (multi‑currency wallets)
* Deposits & withdrawals
* Transfers between accounts
* Transaction audit trail
* Pagination for transaction history

### Fintech‑Grade Guarantees

* **Atomic transfers** using database transactions
* **Idempotency keys** to prevent double‑charging on retries
* **Optimistic locking** to avoid race conditions
* Strong validation & consistent error responses

---

## 🧱 Architecture Overview

```
Controller (REST API)
   ↓
Service (Business logic, transactions)
   ↓
Repository (Spring Data JPA)
   ↓
PostgreSQL
```

Key design principles:

* Controllers are thin
* Business rules live in services
* Entities are persistence‑focused
* DTOs protect API contracts

---

## 🛠 Tech Stack

* **Language:** Java 17
* **Framework:** Spring Boot 3
* **Persistence:** Spring Data JPA + Hibernate
* **Database:** PostgreSQL 16
* **Build Tool:** Maven (with Maven Wrapper)
* **Containerization:** Docker (multi‑stage build)
* **Hosting:** Render (Docker Web Service + PostgreSQL)

---

## 📦 Domain Model

### Account

* Owner name
* Currency (ISO‑4217 string)
* Balance (`BigDecimal`)
* Optimistic lock version

### Transaction

* Immutable audit record
* Types: DEPOSIT, WITHDRAWAL, TRANSFER_DEBIT, TRANSFER_CREDIT
* Balance before / after
* Correlation ID for transfers

### Idempotency Record

* Idempotency key
* Request hash
* Stored response body
* HTTP status code

---

## 🔌 API Overview

### Accounts

| Method | Endpoint                             | Description         |
| ------ | ------------------------------------ | ------------------- |
| POST   | `/api/v1/accounts`                   | Create account      |
| GET    | `/api/v1/accounts/{id}`              | Get account         |
| POST   | `/api/v1/accounts/{id}/deposit`      | Deposit funds       |
| POST   | `/api/v1/accounts/{id}/withdraw`     | Withdraw funds      |
| GET    | `/api/v1/accounts/{id}/transactions` | Transaction history |

### Transfers

| Method | Endpoint            | Description                 |
| ------ | ------------------- | --------------------------- |
| POST   | `/api/v1/transfers` | Transfer funds (idempotent) |

**Header (optional but recommended):**

```
Idempotency-Key: <UUID>
```

---

## 🔐 Data Consistency & Safety

* All balance updates occur inside `@Transactional` service methods
* Optimistic locking via `@Version` prevents lost updates
* Transfers write **two transaction records** (debit + credit) with a shared correlation ID

---

## 🐳 Running Locally

### 1. Start PostgreSQL

```bash
docker-compose up -d
```

### 2. Run Application

```bash
./mvnw spring-boot:run
```

---

## ☁️ Deployment

* Dockerized Spring Boot application
* Deployed to Render as a Docker Web Service
* PostgreSQL hosted on Render
* Environment variables used for configuration

---

## 🔮 Future Improvements

* Authentication & authorization (JWT)
* FX rate integration
* Rate limiting
* Distributed idempotency (Redis)
* Observability (metrics & tracing)
* React frontend (planned)

---

## 👤 Author

Built by **Chai Seng Loi** as a learning‑driven, production‑style fintech project.
