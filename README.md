# 🏦 Enterprise Banking Management System

A full-stack banking management REST API built with **Spring Boot 3.5**, featuring JWT-based authentication, role-based access control, email verification, and scheduled tasks for automated account/transaction management.

---

## 🚀 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5.0 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Database | H2 (in-memory) |
| ORM | Spring Data JPA / Hibernate |
| Mapping | MapStruct 1.5.5 |
| Boilerplate | Lombok |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Email | Spring Mail (Gmail SMTP) |
| Frontend | Thymeleaf + Bootstrap 5 |
| Build | Gradle 8.14 |

---

## ✨ Features

### Authentication & Users
- **Sign up** with email verification (6-digit code sent via Gmail)
- **Login** returns a JWT token valid for 1 hour
- **Resend** verification code
- Role-based access: `USER` and `ADMIN`

### Customers
- Authenticated users can create and view their own customer profile
- Admins can create customers and list all customers (paginated)
- Duplicate FIN code and phone number validation

### Accounts
- Users can create up to **3 accounts** per customer profile
- Accounts expire automatically after 10 years (scheduled nightly job)
- Account statuses: `ACTIVE`, `EXPIRED`, `DELETED`
- Admins can view all accounts filtered by status

### Cards
- Up to **2 cards** per account
- Card statuses: `ACTIVE`, `EXPIRED`, `DELETED`
- Admins can deposit funds directly to a card
- Users can create and activate their own cards

### Transactions
- Card-to-card transfers with real-time balance updates
- **Daily transfer limit:** $1,000 per customer
- **Minimum account balance** enforced: $50
- Customers flagged as `SUSPECTED` if monthly volume exceeds $10,000
- Transaction statuses: `PENDING`, `COMPLETED`, `FAILED`
- Scheduled job processes pending transactions nightly

### Business Rules & Limits

| Rule | Value |
|---|---|
| Max accounts per customer | 3 |
| Max cards per account | 2 |
| Daily transaction limit | $1,000.00 |
| Minimum account balance | $50.00 |
| Monthly suspicion threshold | $10,000.00 |
| Account validity | 10 years |
| Card validity | 5 years |
| JWT expiry | 1 hour |

---

## 📁 Project Structure

```
src/main/java/com/example/bankingprojectfinal/
├── BankingProjectFinalApplication.java    # Entry point
├── Controller/                            # REST controllers
│   ├── AccountController.java
│   ├── CardController.java
│   ├── CustomerController.java
│   └── TransactionController.java
├── Service/
│   ├── Abstraction/                       # Service interfaces
│   └── Concrete/                          # Service implementations
│       ├── AccountServiceImpl.java
│       ├── CardServiceImpl.java
│       ├── CustomerServiceImpl.java
│       ├── TransactionServiceImpl.java
│       └── TransactionSchedule.java       # Scheduled jobs
├── Model/
│   ├── Entity/                            # JPA entities
│   └── Enums/                             # Status & type enums
├── Repository/                            # Spring Data repositories
├── DTOS/                                  # Request/Response DTOs
├── Exception/                             # Custom exceptions & handlers
├── Utils/                                 # Number generators, limit config
├── config/                                # Swagger config
└── security/                              # JWT, auth, user management
    ├── config/
    ├── controller/
    ├── dto/
    ├── model/
    ├── repository/
    └── service/

src/main/resources/
├── application.properties
└── templates/                             # Thymeleaf HTML views
    ├── home.html
    ├── login.html
    ├── customers.html
    ├── accounts.html
    ├── cards.html
    ├── transactions.html
    └── users.html
```

---

## ⚙️ Getting Started

### Prerequisites
- Java 17+
- A Gmail account with an [App Password](https://support.google.com/accounts/answer/185833) for email verification

### 1. Clone the repository

```bash
git clone https://github.com/justpr09rammer/BankingProjectFinalReadyy.git
cd BankingProjectFinalReadyy
```

### 2. Configure environment variables

Set the following before running (or add them to `application.properties`):

```bash
export JWT_SECRET_KEY=your_base64_encoded_secret
```

Add your Gmail credentials to `application.properties`:

```properties
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

### 3. Build and run

```bash
# Linux / macOS
./gradlew bootRun

# Windows
gradlew.bat bootRun
```

The app will start on **http://localhost:8080**

---

## 📖 API Documentation

Once running, access the interactive Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

All protected endpoints require a `Bearer <JWT>` token in the `Authorization` header.

### Key Endpoints

#### Auth — `/api/v1/auth`
| Method | Path | Description |
|---|---|---|
| POST | `/signup` | Register a new user |
| POST | `/login` | Login and receive JWT |
| POST | `/verify` | Verify account with email code |
| POST | `/resend` | Resend verification code |

#### Customers — `/api/v1/customers`
| Method | Path | Role | Description |
|---|---|---|---|
| POST | `/my-profile` | USER | Create own customer profile |
| GET | `/my-profile` | USER | Get own customer profile |
| POST | `/admin/create` | ADMIN | Create a customer |
| GET | `/admin/all` | ADMIN | List all customers (paginated) |

#### Accounts — `/api/v1/accounts`
| Method | Path | Role | Description |
|---|---|---|---|
| POST | `/my-account` | USER | Create own account |
| GET | `/my-accounts` | USER | List own accounts |
| PUT | `/my-accounts/{accountNumber}/activate` | USER | Activate own account |
| GET | `/admin/all` | ADMIN | All accounts (paginated) |
| GET | `/admin/active` | ADMIN | Active accounts (paginated) |
| GET | `/admin/expired` | ADMIN | Expired accounts (paginated) |
| POST | `/admin/customer/{customerId}` | ADMIN | Create account for customer |

#### Cards — `/api/v1/cards`
| Method | Path | Role | Description |
|---|---|---|---|
| POST | `/my-cards/{accountNumber}` | USER | Create card for own account |
| PUT | `/my-cards/activate/{cardNumber}` | USER | Activate own card |
| GET | `/my-cards` | USER | List own cards |
| POST | `` | ADMIN | Create card for any account |
| PUT | `/activate` | ADMIN | Activate any card |
| PUT | `/deposit` | ADMIN | Deposit funds via card |
| GET | `/active` | ADMIN | All active cards (paginated) |

#### Transactions — `/api/v1/transactions`
| Method | Path | Role | Description |
|---|---|---|---|
| POST | `/transfer` | USER | Card-to-card transfer |
| GET | `/my-transactions` | USER | Own transaction history (paginated) |
| GET | `/admin/all` | ADMIN | All transactions (paginated) |
| GET | `/admin/customer/{customerId}` | ADMIN | Transactions by customer |

---

## 🗄️ Database

The application uses an **H2 in-memory database** for development. Access the H2 console at:

```
http://localhost:8080/h2-console
```

| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:test` |
| Username | `sa` |
| Password | `password123` |

> ⚠️ Data is reset every time the application restarts.

---

## 🔐 Security Flow

1. **Register** → email verification code sent
2. **Verify** account using the code
3. **Login** → receive JWT token
4. Include token in all subsequent requests:
   ```
   Authorization: Bearer <your_jwt_token>
   ```

---

## 🕐 Scheduled Jobs

| Job | Schedule | Description |
|---|---|---|
| Expire accounts | Daily at midnight | Marks accounts past their expiry date as `EXPIRED` |
| Process pending transactions | Daily at midnight | Completes or fails `PENDING` transactions |

---

## 🧪 Running Tests

```bash
./gradlew test
```

---
