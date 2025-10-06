# Microservices Project

This repository implements a basic microservices architecture for an e-commerce-like system using Spring Boot. The system is composed of several independent services, each responsible for a separate business domain, and an API gateway for centralized routing and security.

## Architecture Overview

- **API Gateway (`api-gateway`)**: Central entry point for all client requests. Handles routing, authentication (with in-memory users and basic auth), and forwards requests to the appropriate microservice.
- **Discovery Server (`discovery-server`)**: Service registry enabling service discovery for dynamic microservice communication.
- **Customer Service (`customer-service`)**: Manages customer data (CRUD operations).
- **Product Service (`product-service`)**: Handles product catalog, stock management, and product CRUD operations.
- **Order Service (`order-service`)**: Manages customer orders, links customers and products, and handles order CRUD operations.

Each service runs as a standalone Spring Boot application and communicates with others via REST APIs.

## Project Structure

```
microservices-project/
│
├── api-gateway/         # API gateway for routing and security
├── discovery-server/    # Eureka discovery server
├── customer-service/    # Customer management microservice
├── order-service/       # Order management microservice
├── product-service/     # Product management microservice
└── .gitignore
```

## Features

- **API Gateway**
  - Centralized routing for all services
  - Gateway-level security with Basic Auth (admin/user roles)
  - Adds authentication headers to downstream requests

- **Customer Service**
  - CRUD operations for customers
  - Validation and error handling
  - Unit and integration tests

- **Product Service**
  - CRUD operations for products
  - Stock increase/decrease logic for order management
  - Unit and integration tests

- **Order Service**
  - CRUD operations for orders
  - Stores product-quantity mapping for each order
  - Integrates with customer and product services
  - Unit and integration tests

- **Discovery Server**
  - Registers all services for dynamic discovery

## Tech Stack

- Java
- Spring Boot (Web, Data JPA, Security, WebFlux)
- Spring Cloud (Gateway, Eureka Discovery)
- JUnit, Mockito (Testing)
- H2 (in-memory DB for tests)

## Getting Started

### Prerequisites

- Java 17+
- Maven

### Running the System

Start each service in order (in separate terminal windows or via your IDE):

1. **Discovery Server**
   ```
   cd discovery-server
   mvn spring-boot:run
   ```

2. **API Gateway**
   ```
   cd api-gateway
   mvn spring-boot:run
   ```

3. **Customer Service**
   ```
   cd customer-service
   mvn spring-boot:run
   ```

4. **Product Service**
   ```
   cd product-service
   mvn spring-boot:run
   ```

5. **Order Service**
   ```
   cd order-service
   mvn spring-boot:run
   ```

### Default Users (for API Gateway)

- **Admin**: `admin` / `adminpass`
- **User**: `user` / `userpass`

### Example Endpoints

- `POST /customers` - Create a customer
- `GET /products` - List products
- `POST /orders` - Create an order

All endpoints should be accessed via the API Gateway.

## Testing

Each service has unit and integration tests. Run tests with:

```
mvn test
```
inside each microservice directory.

## License

MIT

---

**Note:** This project is for learning and demo purposes, showing how to build a microservices architecture with Spring Boot and Spring Cloud.
