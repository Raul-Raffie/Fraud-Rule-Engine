# Fraud Rule Engine

This project is a microservices-based fraud detection system built with Spring Boot.

## Architecture

The system is split into three services:

- `transaction-service`
  - Generates transaction events
  - Saves every transaction to its own PostgreSQL database
  - Sends transactions to the fraud rule engine
- `fraud-rule-engine-service`
  - Evaluates incoming transactions against fraud rules
  - Forwards fraudulent transactions to the fraud case service
- `fraud-case-service`
  - Persists fraudulent transactions in its own PostgreSQL database
  - Exposes fraud case data for later retrieval

## Databases

The project uses two separate PostgreSQL databases:

- `transaction-service` database
  - Stores all transactions created by the transaction service
- `fraud-case-service` database
  - Stores only fraudulent transactions identified by the fraud rule engine

This keeps normal transaction history separate from fraud-case storage.

## Service Ports

When running locally:

- `transaction-service`: `http://localhost:8080`
- `fraud-rule-engine-service`: `http://localhost:8081`
- `fraud-case-service`: `http://localhost:8082`

## Internal Communication

The services communicate with one another using a shared internal token:

- `Authorization: Bearer ${INTERNAL_SERVICE_AUTH_TOKEN}`

The service base URLs are:

- `FRAUD_RULE_ENGINE_BASE_URL=http://localhost:8081`
- `FRAUD_CASE_SERVICE_BASE_URL=http://localhost:8082`

When running in Docker, Compose overrides those values with container service names so the services can talk to each other correctly inside the network.

## Running With Docker

Start the full stack from the repository root:

```bash
docker compose up --build
```

Useful endpoints:

- `transaction-service` API: `http://localhost:8080`
- `fraud-rule-engine-service` API: `http://localhost:8081`
- `fraud-case-service` API: `http://localhost:8082`

If you want to use a different internal token, set:

```bash
INTERNAL_SERVICE_AUTH_TOKEN=your-secret-token
```

The same token should be used by all services.

## Notes

- `transaction-service` persists the transaction before it is sent to the fraud rule engine.
- `fraud-case-service` keeps the fraud history separate from the main transaction store.
- The project is designed so each service can be developed and run independently in IntelliJ or together through Docker.
