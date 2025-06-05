# 🧠 AI-Powered ATS Resume Checker – Backend

This repository contains the backend for an AI-based resume screening system that compares resumes with job descriptions and generates an ATS score. The backend is built using **Java**, **Spring Boot**, and **PostgreSQL**, and follows a modular architecture with pluggable authentication support.

> ⚙️ This backend was developed by me as part of a collaborative project. The ML model and frontend were handled by teammates.

---

## 🚀 Features

- 📦 **Modular Authentication Framework** using a custom-built plugin-based system.
- 🔐 **JWT Authentication Strategy** for secure API access.
- 📄 **Resume Upload & Parsing** via multipart file APIs.
- 🤖 **Resume Scoring API** that uses a locally integrated **TF-IDF** model to compare resumes with job descriptions.
- 📊 **Detailed Score Breakdown API** (keyword matches, formatting, etc.).
- 🗂️ **File Storage Configuration** for managing uploaded resumes.
- 🧪 Clean architecture with DTOs, exception handling, and environment-specific config.

---

## 🧱 Tech Stack

- **Language**: Java
- **Framework**: Spring Boot
- **Authentication**: Custom Auth Framework with JWT Strategy
- **Database**: PostgreSQL
- **Build Tool**: Maven
- **Other Tools**: Postman, Docker (optional)

---

## 🔧 Installation

### 1. Clone and install the required authentication libraries:

```bash
# Main Auth Framework
git clone https://github.com/Kartikkala/authenticator.git
cd authenticator
mvn install

# JWT Strategy Module
git clone https://github.com/Kartikkala/jwtAuthenticator.git
cd jwtAuthenticator
mvn install
```

### 2. Clone and run this backend project:

```bash
git clone https://github.com/Kartikkala/resume_checker_backend.git
cd resume_checker_backend
mvn spring-boot:run
```

---

## ⚙️ Configuration

Update the following file as per your environment:

**`/src/main/resources/application.properties`**

```properties
# App Info
server.application.name=ResumeCheckerApplication
server.port=8080

# JWT Claim Configuration
jwt.claimFields=email,name
jwt.subjectField=email

# File Upload Settings
file.upload-dir=./uploads/resumes
spring.servlet.multipart.max-file-size=2MB
spring.servlet.multipart.max-request-size=2MB

# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/resumeChecker
spring.datasource.username=postgres
spring.datasource.password=kartik12
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## 🔁 API Overview

### `POST /api/parse-resume`
Uploads and extracts plain text from a `.pdf` resume.

### `POST /api/score`
Accepts resume text and job description text. Returns ATS score and summary.

### `GET /api/score-breakdown/{id}`
Provides detailed breakdown of scoring — keyword matches, sections matched, etc.

---

## 🧠 About the AI Model

The resume scoring uses a **TF-IDF-based similarity model** to compute how well a resume aligns with a given job description. The model is run **locally** and integrated into the backend scoring service via direct method calls.

> The model was developed by a teammate and is currently in early stages.

---

## 🙋‍♂️ Author (Backend)

**Kartik Kala**  
Backend Developer  
[GitHub](https://github.com/Kartikkala) • [LinkedIn](https://www.linkedin.com/in/kartikkala)

---

## 🤝 Contributors

| Name           | Role               |
|----------------|--------------------|
| Kartik Kala    | Backend Development |
| [Friend 1]     | Frontend/UI         |
| [Friend 2]     | Machine Learning    |

---

## 📜 License

This project is released under the [MIT License](LICENSE).
