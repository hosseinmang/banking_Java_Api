# Project Demo Instructions

A recruiter or reviewer can test and demo this project by following these steps:

---

## 1. Clone the Repository
Clone the project from GitHub:

```
git clone https://github.com/hosseinmang/banking_Java_Api.git
cd banking_Java_Api
```

---

## 2. Install Prerequisites
- Java 17 or higher
- Maven 3.6 or higher (already included in this repo as `apache-maven-3.9.5`)

---

## 3. Build the Project
Open PowerShell in the project root and run:

```
.\apache-maven-3.9.5\bin\mvn.cmd clean install
```

---

## 4. Run the Application
Start the API with:

```
.\apache-maven-3.9.5\bin\mvn.cmd spring-boot:run
```

The API will be available at: [http://localhost:8080/api](http://localhost:8080/api)

---

## 5. Test the API Endpoints
Use Postman, curl, or any REST client. Example requests:

### Register a User
```
curl -X POST "http://localhost:8080/api/auth/signup" `
  -H "Content-Type: application/json" `
  -d '{"username":"testuser","email":"test@email.com","password":"Test@1234"}'
```

### Log In and Get JWT Token
```
curl -X POST "http://localhost:8080/api/auth/signin" `
  -H "Content-Type: application/json" `
  -d '{"username":"testuser","password":"Test@1234"}'
```
Copy the `token` from the response for use in the next requests.

### Create a Bank Account (Authenticated)
```
curl -X POST "http://localhost:8080/api/accounts" `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer <PASTE_YOUR_TOKEN_HERE>" `
  -d '{"accountName":"My Savings","accountType":"SAVINGS"}'
```

---

## 6. Run Automated Tests
To run all unit and integration tests:

```
.\apache-maven-3.9.5\bin\mvn.cmd test
```

---

## 7. (Optional) Access the H2 Database Console
Visit [http://localhost:8080/api/h2-console](http://localhost:8080/api/h2-console) and use the JDBC URL `jdbc:h2:mem:bankingdb`.

---

**For more details, see the README.md or contact the project owner.**