# 🛒 E-Commerce Backend System

A **production-grade backend system for an e-commerce platform** built using **Java, Spring Boot, and MySQL**.
This project provides secure REST APIs for managing **users, products, shopping carts, orders, payments, and inventory**.

The system follows **industry-standard layered architecture** and demonstrates professional backend development practices including **JWT authentication, role-based authorization, pagination, DTO usage, and global exception handling**.

---

# 🚀 Features

### 👤 User Management

* User registration
* User login with **JWT Authentication**
* Update user profile
* Delete users (Admin only)
* Role-based access (**ADMIN, CUSTOMER**)

### 📦 Product Management

* Add product (Admin)
* Update product (Admin)
* Delete product (Admin)
* View product list
* Pagination and sorting support
* View product by ID

### 🛒 Shopping Cart

* Add product to cart
* Update cart item quantity
* Remove item from cart
* View cart items
* Automatic cart total calculation

### 📑 Order Management

* Convert cart to order (Checkout)
* Order history for users
* Get order by ID
* Update order status (Admin)

### 💳 Payment Simulation

* Simulate payment **SUCCESS / FAILED**
* Order status updated based on payment result

### 📊 Inventory Management

* Automatically reduce stock after successful order
* Prevent ordering out-of-stock products

### 📧 Email Notification

* Sends email confirmation after successful order placement

### 🔐 Security

* Spring Security
* JWT Authentication
* Role-based authorization
* Stateless session management

### ⚠️ Exception Handling

* Global exception handler
* Custom exceptions:

  * ResourceNotFoundException
  * BadRequestException
  * ProductOutOfStockException
  * Validation errors
  * Authentication errors

---

# 🛠️ Tech Stack

| Component  | Technology                  |
| ---------- | --------------------------- |
| Language   | Java 17                     |
| Framework  | Spring Boot 3               |
| Security   | Spring Security + JWT       |
| ORM        | Spring Data JPA / Hibernate |
| Database   | MySQL                       |
| Build Tool | Maven                       |
| Validation | Jakarta Validation          |
| Email      | Spring Mail                 |
| Testing    | JUnit 5 & Mockito           |
| Logging    | SLF4J / Logback             |

---

# 🏗️ Project Architecture

The project follows a **layered architecture**:

```
Controller → Service → Repository → Entity
```

### Package Structure

```
com.ecommerce
 ┣ controller
 ┣ service
 ┣ repository
 ┣ entity
 ┣ dto
 ┣ config
 ┣ filter
 ┣ exception
```

---

# 📂 Database Schema

### User

| Field    | Type             |
| -------- | ---------------- |
| id       | Long             |
| name     | String           |
| email    | String           |
| password | String           |
| role     | ADMIN / CUSTOMER |

---

### Product

| Field       | Type   |
| ----------- | ------ |
| id          | Long   |
| name        | String |
| description | String |
| price       | double |
| stock       | int    |
| category    | String |
| imageUrl    | String |
| rating      | double |

---

### Cart

| Field      | Type   |
| ---------- | ------ |
| id         | Long   |
| user_id    | FK     |
| totalPrice | double |

---

### CartItem

| Field      | Type |
| ---------- | ---- |
| id         | Long |
| cart_id    | FK   |
| product_id | FK   |
| quantity   | int  |

---

### Order

| Field         | Type                                     |
| ------------- | ---------------------------------------- |
| id            | Long                                     |
| user_id       | FK                                       |
| totalAmount   | double                                   |
| orderDate     | LocalDateTime                            |
| paymentStatus | PENDING / SUCCESS / FAILED               |
| orderStatus   | PLACED / SHIPPED / DELIVERED / CANCELLED |

---

### OrderItem

| Field      | Type   |
| ---------- | ------ |
| id         | Long   |
| order_id   | FK     |
| product_id | FK     |
| quantity   | int    |
| price      | double |

---

# 🔑 Authentication

JWT-based authentication is used.

### Register User

```
POST /api/users/register
```

### Login

```
POST /api/users/login
```

Returns:

```
JWT Token
```

Include the token in requests:

```
Authorization: Bearer <token>
```

---

# 📡 API Endpoints

## User APIs

| Method | Endpoint            | Description         |
| ------ | ------------------- | ------------------- |
| POST   | /api/users/register | Register new user   |
| POST   | /api/users/login    | Login user          |
| GET    | /api/users/{id}     | Get user            |
| PUT    | /api/users/{id}     | Update user         |
| DELETE | /api/users/{id}     | Delete user (Admin) |

---

## Product APIs

| Method | Endpoint           | Description            |
| ------ | ------------------ | ---------------------- |
| POST   | /api/products      | Add product (Admin)    |
| GET    | /api/products      | Get all products       |
| GET    | /api/products/{id} | Get product            |
| PUT    | /api/products/{id} | Update product (Admin) |
| DELETE | /api/products/{id} | Delete product (Admin) |

Supports:

```
/api/products?page=0&size=10&sortBy=price&direction=asc
```

---

## Cart APIs

| Method | Endpoint                                 |
| ------ | ---------------------------------------- |
| GET    | /api/cart                                |
| POST   | /api/cart/add/{productId}?quantity=1     |
| PUT    | /api/cart/update/{cartItemId}?quantity=2 |
| DELETE | /api/cart/remove/{cartItemId}            |

---

## Order APIs

| Method | Endpoint                |
| ------ | ----------------------- |
| POST   | /api/orders/checkout    |
| GET    | /api/orders             |
| GET    | /api/orders/{id}        |
| PUT    | /api/orders/{id}/status |

---

# 📧 Email Notification

After successful order placement, the system sends a confirmation email using **Spring Mail**.

Example email:

```
Subject: Order Confirmation

Your order #123 has been successfully placed.
```

---

# ⚙️ How to Run Locally

### 1️⃣ Clone Repository

```
git clone https://github.com/Laxmidhar_Ojha/ecommerce-backend-system.git
```

```
cd ecommerce-backend-system
```

---

### 2️⃣ Create MySQL Database

```
CREATE DATABASE ecommerce_db;
```

---

### 3️⃣ Configure application.properties

```
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
spring.datasource.username=root
spring.datasource.password=password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=gmail
spring.mail.password=app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

### 4️⃣ Run Application

Using Maven:

```
mvn spring-boot:run
```

Application runs at:

```
http://localhost:8080
```

---

# 🧪 Testing APIs

You can test APIs using:

* Postman

Example request:

```
POST /api/cart/add/1?quantity=2
```

---

# 📬 Postman Collection

All APIs can be tested using the provided **Postman Collection**.

Import:

```
postman/ecommerce-api.postman_collection.json
```

---

# 📌 Key Highlights

✔ Clean layered architecture
✔ JWT authentication
✔ Role-based security
✔ Pagination & sorting
✔ DTO pattern
✔ Global exception handling
✔ Email notifications
✔ Inventory management

---
