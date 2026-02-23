EcoPilot Microservices Backend
==============================

This repository contains the backend microservices and infrastructure for the **EcoPilot** platform. It is organized as a set of Spring Boot, Node.js and Python services orchestrated via Docker Compose, with centralized configuration, service discovery, security and monitoring.

Table of Contents
-----------------
- Architecture Overview
- Services
  - Infrastructure
  - Business Microservices
  - Support & Tooling
- Technology Stack
- Running the Stack with Docker
- Configuration & Security
- Observability & Tooling
- Useful Endpoints

Architecture Overview
---------------------

At a high level, the platform is composed of:

- **API Gateway**: Single entry point exposing REST APIs to the frontend, routing requests to downstream services.
- **Service Discovery**: Eureka server that registers all Spring Boot microservices.
- **Configuration Server**: Spring Cloud Config server serving configuration from a local Git‑like repo (`config-repo`).
- **Business Microservices**: Domain services for users, articles, projects, suppliers, notifications and import.
- **Data Stores**: PostgreSQL (relational data) and MongoDB (notifications & events).
- **Messaging**: Kafka for event-driven communication between services.
- **Identity & Access Management**: Keycloak for authentication and authorization.
- **Monitoring & Tracing**: Prometheus, Grafana and Zipkin, plus additional admin GUIs.

The full composition is defined in `docker-compose.yml` at the root of this workspace.

Services
--------

### Infrastructure

- **discovery-service**
  - Location: `infrastructure/eureka-server`
  - Role: Eureka Server for service discovery.
  - Port: `8761`

- **config-service**
  - Location: `infrastructure/config-server`
  - Role: Spring Cloud Config Server (native file-based backend).
  - Port: `8888`
  - Loads configuration from the `config-repo` directory.

- **gateway-service**
  - Location: `infrastructure/api-gateway`
  - Role: API Gateway, routing HTTP requests to backend services and handling CORS.
  - Port: `8080`
  - Routes are configured in:
    - `infrastructure/api-gateway/src/main/resources/application.yml`
    - `config-repo/api-gateway.yml`

- **Key supporting infrastructure**
  - **Postgres** (`postgres`): main relational database, with schema initialization in `scripts/init-databases.sql`.
  - **MongoDB** (`mongo`): document store for notifications.
  - **Kafka & Zookeeper** (`kafka`, `zookeeper`): message broker for event-driven communication.
  - **Keycloak** (`keycloak`): identity provider (realm definition in `keycloak-config/realm-export.json`).

### Business Microservices

All business services are located under the `services` directory and are packaged as Docker images using their respective `Dockerfile`.

- **user-service**
  - Location: `services/user-service`
  - Stack: Java 17, Spring Boot.
  - Responsibilities: user management and authentication integration with Keycloak and JWT.
  - Exposed through API Gateway under `/api/auth/**` and `/api/users/**`.

- **article-service**
  - Location: `services/article-service`
  - Stack: Java 17, Spring Boot.
  - Responsibilities: manage catalog articles and their hierarchy levels (`Niveau1`–`Niveau6`), pricing strategies, etc.
  - Exposed through API Gateway under `/api/articles/**`, `/api/pending-articles/**`, `/api/niveaux/**`.

- **project-service**
  - Location: `services/project-service`
  - Stack: Java 17, Spring Boot.
  - Responsibilities:
    - Manage projects (`Projet`), clients, lots, works (`Ouvrage`), structures and project articles.
    - Provide aggregated project details and pricing through endpoints such as `/api/projet-details/{id}/details`.
  - Exposed through API Gateway under `/api/projets/**`, `/api/projet-details/**`, `/api/ouvrages/**`, `/api/blocs/**`, `/api/clients/**`.

- **fournisseur-service**
  - Location: `services/fournisseur-service`
  - Stack: Java 17, Spring Boot.
  - Responsibilities: manage suppliers (`Fournisseur`) and related data.
  - Exposed through API Gateway under `/api/fournisseurs/**`.

- **notification-service**
  - Location: `services/notification-service`
  - Stack: Node.js (Express, Mongoose, KafkaJS, Socket.IO).
  - Responsibilities:
    - Consume domain events from Kafka.
    - Persist notifications and events in MongoDB.
    - Provide real-time notifications to clients via WebSocket (Socket.IO).
  - Main entrypoint: `server.js`.

- **import-service**
  - Location: `services/import-service`
  - Stack: Python (FastAPI, Uvicorn, Pandas, OpenPyXL).
  - Responsibilities:
    - Import and parse DPGF/Excel files.
    - Provide endpoints for previewing sheets and parsing project data:
      - `POST /api/projets/preview-dpgf`
      - `POST /api/projets/parse-dpgf`
  - Exposed directly from the API Gateway to the frontend.

### Support & Tooling

The following containers are provided primarily for development and troubleshooting:

- **pgadmin**: Web UI for PostgreSQL on port `5050`.
- **mongo-express**: Web UI for MongoDB on port `8089`.
- **kafdrop**: Web UI for Kafka topics and consumers on port `9000`.

Technology Stack
----------------

- **Languages**
  - Java 17 (Spring Boot microservices)
  - Python 3.9 (FastAPI import service)
  - Node.js (Notification service)

- **Frameworks & Libraries**
  - Spring Boot, Spring Cloud (Eureka, Gateway, Config Server)
  - FastAPI, Uvicorn, Pandas, OpenPyXL
  - Express, Mongoose, KafkaJS, Socket.IO

- **Data & Messaging**
  - PostgreSQL (multiple databases for users, articles, projects, suppliers)
  - MongoDB (notifications)
  - Kafka (event streaming)

- **Security & IAM**
  - Keycloak for authentication and authorization
  - JWT-based security for backend services

- **Observability**
  - Prometheus for metrics
  - Grafana for dashboards
  - Zipkin for distributed tracing

Running the Stack with Docker
-----------------------------

### Prerequisites

- Docker and Docker Compose installed locally.
- Ports used by the stack are free on your machine (e.g. 8080, 8081–8088, 8761, 8888, 9090, 3000, 9000, 9411, 5050, 8089, 8180, 5432, 27017).

### Start All Services

From the repository root (`microservices-ecopilot`), run:

```bash
docker-compose up --build
```

This will:

- Build all custom images (gateway, discovery, config, business services).
- Start databases, Kafka, Keycloak and monitoring stack.
- Expose the API Gateway on `http://localhost:8080`.

To run in detached mode:

```bash
docker-compose up --build -d
```

### Stop All Services

```bash
docker-compose down
```

To remove volumes as well (Postgres and Mongo data), add `-v`:

```bash
docker-compose down -v
```

Configuration & Security
------------------------

- **Centralized configuration**
  - All Spring Boot services read configuration from the Config Server (`config-service`).
  - Config Server serves files from the `config-repo` directory, such as:
    - `config-repo/api-gateway.yml`
    - `config-repo/article-service.yml`
    - `config-repo/project-service.yml`
    - `config-repo/fournisseur-service.yml`
    - `config-repo/user-service.yml`

- **Environment variables**
  - Service-specific environment variables (database URLs, Kafka bootstrap servers, Keycloak client secrets, JWT secrets, etc.) are defined in `docker-compose.yml`.
  - For local development outside Docker, equivalent variables should be configured in your IDE or `application.yml`/`.env` files.

- **Keycloak**
  - The Keycloak container imports realm configuration from `keycloak-config/realm-export.json`.
  - By default it exposes an admin interface on `http://localhost:8180`.

Observability & Tooling
-----------------------

- **Prometheus**
  - Container: `prometheus`
  - URL: `http://localhost:9090`
  - Scrapes metrics from services on `/actuator/prometheus`.

- **Grafana**
  - Container: `grafana`
  - URL: `http://localhost:3000`
  - Default admin user: `admin` (password configured in `docker-compose.yml`).

- **Zipkin**
  - Container: `zipkin`
  - URL: `http://localhost:9411`
  - Receives tracing data from configured services.

- **Admin UIs**
  - Eureka Dashboard (Service Registry): `http://localhost:8761`
  - Config Server Health: `http://localhost:8888/actuator/health`
  - API Gateway: `http://localhost:8080`
  - PGAdmin: `http://localhost:5050`
  - Mongo Express: `http://localhost:8089`
  - Kafdrop: `http://localhost:9000`
  - Keycloak: `http://localhost:8180`

Useful Endpoints
----------------

Below is a non-exhaustive list of important HTTP endpoints exposed through the API Gateway:

- **Authentication & Users**
  - `/api/auth/**`
  - `/api/users/**`

- **Articles & Catalog**
  - `/api/articles/**`
  - `/api/pending-articles/**`
  - `/api/niveaux/**`

- **Projects & Structure**
  - `/api/projets/**`
  - `/api/projet-details/**`
  - `/api/ouvrages/**`
  - `/api/blocs/**`
  - `/api/clients/**`

- **Suppliers**
  - `/api/fournisseurs/**`

- **Import**
  - `/api/projets/preview-dpgf/**`
  - `/api/projets/parse-dpgf/**`

For complete details, consult the individual controllers under the `services/*-service/src/main/java` and the gateway route configuration.
