# Banking API

A secure, enterprise-grade banking API built with Java 17 and Spring Boot, demonstrating best practices in authentication, transaction management, and layered architecture.

---

## Executive Summary (Elevator Pitch)
**What:** This is a secure, enterprise-grade banking API built with Java 17 and Spring Boot, demonstrating best practices in authentication, transaction management, and layered architecture.

**Why:** It solves real-world banking challenges: secure user management, robust transaction processing, and scalable design.

---

## Key Features
- JWT-based authentication and role-based access control
- Transactional integrity for all financial operations
- Comprehensive error handling and validation
- Modular, layered architecture for maintainability and scalability
- Unit and integration tests for reliability

---

## Technical Highlights
- **Security:** JWT, BCrypt password hashing, role-based authorization
- **Transaction Management:** Uses `@Transactional` for atomic operations
- **API Design:** RESTful endpoints, DTOs for clean data transfer
- **Testing:** JUnit and Mockito for service and controller tests
- **Documentation:** Clear README with setup, usage, and code walkthrough

---

## Business Value
This project demonstrates how to build a banking backend that is secure, reliable, and ready for real-world scaling—exactly what a modern financial institution needs.

---

## How to Review the Code
Start with the README for an overview, then look at the model, service, and controller layers to see how business logic and security are implemented. The test suite shows a commitment to code quality.

---

## Demo/Usage
You can run the project locally, register a user, log in, create accounts, and perform transactions—all with secure, validated APIs.

---

## What This Project Does

This project implements a secure, enterprise-grade banking API using Java 17 and Spring Boot. It provides:
- **User registration and authentication** (JWT-based)
- **Account management** (create, view, and manage bank accounts)
- **Transaction processing** (transfers, deposits, withdrawals)
- **Role-based access control** (USER and ADMIN roles)
- **Comprehensive validation and error handling**
- **Unit and integration testing**

The API is designed to demonstrate best practices in Java enterprise development, including security, transaction management, and layered architecture.

---

## How the Code Works (Code Walkthrough)

### 1. Domain Models
- **User, Role, Account, Transaction**: Core entities with JPA relationships.
- **ERole, AccountType, TransactionType, TransactionStatus**: Enums for type safety.

### 2. Security Layer
- **JWT Authentication**: Users authenticate via `/api/auth/signin` and receive a JWT token. All protected endpoints require this token.
- **WebSecurityConfig**: Configures security, password encoding, and endpoint access rules.
- **UserDetailsServiceImpl, JwtUtils, AuthTokenFilter**: Handle user details, JWT creation/validation, and request filtering.

### 3. Repository Layer
- **JPA Repositories**: `UserRepository`, `RoleRepository`, `AccountRepository`, `TransactionRepository` provide CRUD and custom queries.

### 4. Service Layer
- **UserService, AccountService, TransactionService**: Business logic, `@Transactional` for data integrity, business rules enforcement.

### 5. Controller Layer
- **AuthController**: Signup and login.
- **AccountController**: Account creation and queries.
- **TransactionController**: Deposits, withdrawals, and transfers.

### 6. Exception Handling
- **GlobalExceptionHandler**: Standardizes error responses.
- **Custom Exceptions**: For resource not found, insufficient funds, etc.

### 7. Database Initialization
- **DatabaseInitializer**: On startup, creates default roles if they don't exist.

### 8. Testing
- **JUnit and Mockito**: Unit testing for service logic.

---

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Running the Application
1. Clone the repository
2. Build the project using Maven:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
4. The API will be available at `http://localhost:8080/api`

---

## API Endpoints (Summary)

### Authentication
- `POST /api/auth/signup` - Register a new user
- `POST /api/auth/signin` - Authenticate user and get JWT token

### Accounts
- `GET /api/accounts` - List all accounts of the authenticated user
- `POST /api/accounts` - Create a new account

### Transactions
- `POST /api/transactions/transfer` - Transfer funds between accounts
- `POST /api/transactions/deposit/{accountNumber}` - Deposit funds to an account
- `POST /api/transactions/withdraw/{accountNumber}` - Withdraw funds from an account

---

## Security Implementation (Summary)
- JWT-based authentication
- Role-based access control (USER and ADMIN)
- Secure password storage (BCrypt)

---

## Testing
To run the tests:
```bash
mvn test
```

---

## Future Enhancements
- Implement OAuth2 for more robust authentication
- Add real-time notifications for transactions
- Implement rate limiting to prevent abuse
- Add comprehensive audit logging
- Migrate to a clustered database for high availability
