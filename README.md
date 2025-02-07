# Spring Boot

This is a Spring Boot application for connecting to a database.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Build and Run Locally](#build-and-run-locally)
3. [Deploy with Docker](#deploy-with-docker)
4. [Configuration](#configuration)
5. [Troubleshooting](#troubleshooting)
6. [SpringSecurity](#springSecurity)

---

## Prerequisites

- Java 17 or higher
- Maven 3.x
- Docker (for containerized deployment)
- PostgreSQL (or any other database you're using)

---

## Build and Run Locally

1. package the repo:
   ````bash
   .\mvnw clean package
2. Build the image:
   ```
   docker build -t my-spring-app .
3. Clone the repository:
   ```bash
   docker run -d \
    --name spring-app \
    --network my-network \
    -e SPRING_PROFILES_ACTIVE=prod \
    -e DB_URL=jdbc:postgresql://postgres-db:5432/mydatabase \
    -e DB_USERNAME=postgres \
    -e DB_PASSWORD=password \
    -p 8080:8080 \
    myapplicationimage

> **N.B.**:Ensure that the server name in db_url (postgres-db in this case) is the container name.

> **N.B.**: Ensure that the `ddl-auto` property is set to `update` during initial deployment to automatically create the
> required database tables.

## Authentication & Authorization with spring security
### Spring Security:
sécurise les requêtes HTTP de votre application web en les faisant passer par trois niveaux :

- Premièrement, le **pare-feu HTTP** bloque les requêtes suspectes.

- Deuxièmement, le **proxy (DelegatingFilterProxy)** prend en charge le reste des requêtes HTTP, et les envoie vers la
  chaîne de filtres de Spring Security.

- Enfin, les **filtres de la chaîne (Filter Chain)** s’assurent que ces requêtes HTTP soient conformes à leurs critères de sécurité.

### OAuth 2.0
**OAuth 2.0** is an authorization framework that allows third-party applications to access user data without needing the user's credentials (like their username and password). Instead, it uses tokens to grant limited access to resources.

**OpenID Connect** is an identity layer that works with OAuth 2.0. While OAuth 2.0 is primarily about authorization (granting access to resources), OpenID Connect is about authentication (verifying the user's identity) and providing user information.

```diagram
+----------+                         +----------------+          +---------------+
|          |                         |                |          |               |
|  Client  |                         | Authorization  |          |   Resource    |
| (Browser)|                         |     Server     |          |    Server     |
|          |                         | (OAuth2 + OIDC)|          | (Your App API)|
+----+-----+                         +-------+--------+          +-------+-------+
     |                                      |                            |
     | 1. Initiate Auth Request             |                            |
     |------------------------------------->|                            |
     |                                      |                            |
     | 2. Redirect to Login Page            |                            |
     |<-------------------------------------|                            |
     |                                      |                            |
     | 3. User Authenticates (OIDC)         |                            |
     |------------------------------------->|                            |
     |                                      |                            |
     | 4. Authorization Code Returned       |                            |
     |<-------------------------------------|                            |
     |                                      |                            |
     | 5. Exchange Code for Tokens          |                            |
     |------------------------------------->|                            |
     |                                      |                            |
     | 6. Return Access + ID Tokens         |                            |
     |<-------------------------------------|                            |
     |                                      |                            |
     | 7. Request Protected Resource        |                            |
     |--------------------------------------------------------------->  |
     |                                      |                            |
     | 8. Validate Token & Return Resource  |                            |
     |<---------------------------------------------------------------  |
     |                                      |                            |
+----+-----+                         +-------+--------+          +-------+-------+
|          |                         |                |          |               |
|  Client  |                         | Authorization  |          |   Resource    |
| (Browser)|                         |     Server     |          |    Server     |
|          |                         | (OAuth2 + OIDC)|          | (Your App API)|
+----------+                         +----------------+          +---------------+                                 +----------------+
``` 

```mermaid
sequenceDiagram
participant User
participant Browser
participant Client as Spring App (Client)
participant AS as Authorization Server
participant RS as Resource Server
participant DB as User Database

    Note over User,DB: Authorization Code Flow with PKCE
    
    % Initial Request
    User->>Browser: Access Protected Resource
    Browser->>Client: GET /protected-resource
    
    % PKCE Generation
    Note over Client: Generate Code Verifier<br/>Generate Code Challenge<br/>(SHA-256 hash of verifier)
    
    % Authorization Request
    Client->>Browser: Redirect to Auth Server
    Note right of Client: With parameters:<br/>- client_id<br/>- redirect_uri<br/>- response_type=code<br/>- scope (openid, profile, etc)<br/>- state (CSRF token)<br/>- code_challenge<br/>- code_challenge_method=S256
    
    Browser->>AS: GET /authorize
    AS->>DB: Check if user is authenticated
    
    alt User not authenticated
        AS->>Browser: Show login form
        Browser->>User: Display login page
        User->>Browser: Enter credentials
        Browser->>AS: POST /login
        AS->>DB: Validate credentials
    end
    
    % Consent
    AS->>Browser: Display consent screen
    Browser->>User: Show requested permissions
    User->>Browser: Approve consent
    Browser->>AS: POST /consent
    
    % Authorization Code
    AS->>Browser: Redirect to redirect_uri
    Note right of AS: With parameters:<br/>- authorization_code<br/>- state (validate)
    
    Browser->>Client: GET /callback
    
    % Token Exchange
    Client->>AS: POST /token
    Note right of Client: With parameters:<br/>- grant_type=authorization_code<br/>- code<br/>- redirect_uri<br/>- client_id<br/>- code_verifier
    
    AS->>AS: Verify code & PKCE
    AS->>Client: Return tokens
    Note right of AS: Returns:<br/>- access_token<br/>- id_token (JWT)<br/>- refresh_token<br/>- token_type<br/>- expires_in
    
    % User Info (OpenID Connect)
    Client->>AS: GET /userinfo
    Note right of Client: Bearer access_token
    AS->>Client: Return user claims
    
    % Resource Access
    Client->>RS: API request
    Note right of Client: Bearer access_token
    RS->>AS: Validate token
    RS->>Client: Protected resource data
    
    % Token Refresh
    Note over Client,AS: When access token expires
    Client->>AS: POST /token
    Note right of Client: With parameters:<br/>- grant_type=refresh_token<br/>- refresh_token<br/>- client_id
    AS->>Client: New access & refresh tokens
