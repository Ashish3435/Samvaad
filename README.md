# 💬 Samvaad Backend

> Modern real-time chat application backend built with **Spring Boot**, **WebSocket (STOMP)**, **JWT Authentication**, and **PostgreSQL**.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen)
![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-success)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## ✨ Features

- 🔐 JWT Authentication
- 👤 User Registration & Login
- 💬 One-to-One Chat
- 👥 Group Chat
- ⚡ Real-time Messaging using WebSocket (STOMP)
- 🟢 Online User Presence
- 💾 PostgreSQL Database
- 🛡 Spring Security
- 🌐 REST APIs
- 📡 Message Persistence

---

## 🛠 Tech Stack

### Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- JWT
- WebSocket (STOMP)
- PostgreSQL
- Maven

---

# 📂 Project Structure

```
src
├── main
│   ├── java
│   │   └── com
│   │       └── ashish
│   │           └── samvaad
│   │               ├── config
│   │               ├── controller
│   │               ├── dto
│   │               ├── entity
│   │               ├── repository
│   │               ├── security
│   │               ├── service
│   │               ├── websocket
│   │               └── SamvaadApplication.java
│   └── resources
│       └── application.properties
```

---

# 🚀 Getting Started

## Clone Repository

```bash
git clone https://github.com/Ashish3435/Samvaad.git

cd Samvaad
```

---

## Configure Environment

Update your database credentials inside

```
application.properties
```

Example

```properties
spring.datasource.url=YOUR_DB_URL
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

jwt.secret=YOUR_SECRET
```

---

## Run Application

```bash
mvn spring-boot:run
```

Backend runs on

```
http://localhost:8083
```

---

# 📦 Build

```bash
mvn clean install
```

---

# 🗺 Roadmap

## ✅ V1.0.0

- JWT Authentication
- Spring Security
- User Registration & Login
- Personal Chat
- Group Chat
- WebSocket Messaging
- PostgreSQL Integration

---

## 🚧 V2 (In Progress)

- 📞 Voice Calling
- 🎥 Video Calling
- 📁 Media Sharing
- 😊 Emoji Reactions
- ✍ Typing Indicator
- ✔ Read Receipts
- 🔔 Push Notifications
- ⚡ Performance Improvements

---

# 🏷 Current Stable Release

**v1.0.0**

Released: July 2026

---

# 👨‍💻 Author

**Ashish Prajapati**

GitHub

https://github.com/Ashish3435

---

# ⭐ Support

If you found this project useful,

⭐ Star the repository

🍴 Fork it

🚀 Build something awesome!
