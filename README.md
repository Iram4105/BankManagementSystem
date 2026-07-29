# 🏦 Bank Management System

A backend-based **Bank Management System** developed using **Java, Spring Boot, Spring Security, JWT Authentication, MySQL, and Maven**. This project provides secure REST APIs for managing bank accounts and transactions while following a layered architecture.

> 🚧 **Project Status:** Backend development is in progress. Frontend integration is planned for future updates.

---

## 🚀 Features

- User Registration
- User Login
- JWT Authentication
- Spring Security Integration
- Role-Based Access (Planned)
- Account Management
- Deposit Money
- Withdraw Money
- Balance Inquiry
- RESTful APIs
- Exception Handling
- Database Integration using MySQL

---

## 🛠 Tech Stack

### Backend
- Java 17+
- Spring Boot
- Spring Security
- JWT (JSON Web Token)
- Spring Data JPA
- Hibernate
- Maven

### Database
- MySQL

### Tools
- Eclipse IDE
- Git
- GitHub
- Postman

---

## 📂 Project Structure

```
src
 ├── controller
 ├── dto
 ├── entity
 ├── repository
 ├── security
 ├── service
 ├── exception
 └── config
```

---

## 🔐 Authentication

This project uses **JWT Authentication** to secure REST APIs.

Authentication Flow:

1. User registers.
2. User logs in.
3. JWT Token is generated.
4. Token is sent in the Authorization header.
5. Protected APIs validate the token before processing requests.

---

## 📌 REST APIs

### Authentication
- Register User
- Login User

### Banking
- Create Account
- Deposit Money
- Withdraw Money
- Check Balance

---

## 💾 Database

- MySQL
- Spring Data JPA
- Hibernate ORM

---

## 📈 Future Enhancements

- React Frontend
- Transaction History
- Fund Transfer
- Email Notifications
- Password Reset
- Admin Dashboard
- Docker Deployment
- Unit & Integration Testing
- Swagger API Documentation

---

## ▶️ How to Run

1. Clone the repository

```bash
git clone https://github.com/Iram4105/BankManagementSystem.git
```

2. Open the project in Eclipse.

3. Configure MySQL in `application.properties`.

4. Run the project as a Spring Boot Application.

5. Test APIs using Postman.

---

## 📸 Screenshots

Screenshots will be added after frontend development.

---

## 👩‍💻 Author

**Iram**

B.Tech Computer Science Engineering

GitHub: https://github.com/Iram4105

---

## ⭐ Support

If you find this project useful, consider giving it a ⭐ on GitHub.
