# Project Name: Secure Spring Boot Application

## Overview:
This project is a secure Spring Boot application developed to ensure robust security measures and comprehensive testing. It utilizes various technologies including Spring Boot 3, Spring Security 6, KeyCloak, AspectJ, JUnit 5 & Mockito, and Testcontainers.
## Table of contents
- [Features](#features)
- [Technologies Used](#technologies-used)
- [How to Use](#how-to-use)
  
## Features:
- **AOP Aspects with Custom Annotations:** Engineered Aspect-Oriented Programming (AOP) aspects with custom annotations for log auditing and file authority checks, ensuring comprehensive auditing and access control.
- **Security Measures:** Implemented security measures using Spring Security 6 and KeyCloak, ensuring secure authentication and authorization processes.
- **User and Admin Endpoints:** Implemented user and admin endpoints to manage application functionality securely.
- **Unit and Integration Testing:** Conducted thorough unit and integration tests using JUnit 5, Mockito, and Testcontainers for both Postgres and Keycloak, achieving an 82% code coverage to ensure robustness and reliability.
- **API Documentation:** Documented API endpoints using Swagger to provide clear and comprehensive documentation for API usage understanding.
- **Secure Data Transmission:** Ensured secure transmission of data and credentials over HTTPS, prioritizing data integrity and confidentiality.

## Technologies Used:
- Java 17
- Spring Boot 3
- Spring Security 6
- KeyCloak
- AspectJ
- JUnit 5 & Mockito
- Docker & Docker Compose
- Testcontainers
- Swagger

## How to Use:
1. Clone the repository to your local machine.
   
   ```bash
   git clone https://github.com/zeyadahmed10/Secure-File-Access-System-With-Log-Auditing.git
2. Compile project to generate QClass for Querydsl

     ```bash
     mvn clean compile
3. Run docker-compose for Postgres and Keycloak

     ```bash
     docker-compose up -d
       
5. Configure KeyCloak for authentication and authorization as per project requirements.
6. Build and run the application using Maven or your preferred IDE.
7. Access the API endpoints documented with Swagger for testing and usage understanding.
8. Execute unit and integration tests to ensure functionality and reliability.

