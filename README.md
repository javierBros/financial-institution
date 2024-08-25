## Technologies Used

- **Java 17**
- **Spring Boot 3.1.1**
- **Spring Data JPA**
- **Hibernate**
- **MySQL**
- **JUnit 5**
- **Mockito**
- **SLF4J** for logging

## Prerequisites

- **Java 17** or later
- **MySQL** database
- **Gradle** build tool

## Postman project

The Postman project with all endpoints has been attached in src/main/resources/Financial_distribution.postman_collection.json

## Required Environment Variables for Database Configuration

To properly configure the database for this project, you need to set the following environment variables:

- **`DB_URL`**: The JDBC URL for your MySQL database.
    - Example: `jdbc:mysql://localhost:3306/financial_institution`

- **`DB_USERNAME`**: The username for connecting to the MySQL database.
    - Example: `root`

- **`DB_PASSWORD`**: The password associated with the MySQL database username.
    - Example: `yourpassword`

- **`DB_DRIVER`**: The driver class name for MySQL.
    - Example: `com.mysql.cj.jdbc.Driver`

### Environment Variables for OAuth2 Client

The application requires the following environment variables for OAuth2 authentication:

- `CLIENT_ID`: The client ID for OAuth2 authentication. Default is `default-client-id`.
- `CLIENT_SECRET`: The client secret for OAuth2 authentication. Default is `default-secret`.

## API Endpoints

### Clients

- **GET /api/clients**: Retrieve all clients
- **GET /api/clients/{id}**: Retrieve a client by ID
- **POST /api/clients**: Create a new client
- **PUT /api/clients/{id}**: Update a client by ID
- **DELETE /api/clients/{id}**: Delete a client by ID

### Products

- **GET /api/products**: Retrieve all products
- **GET /api/products/{id}**: Retrieve a product by ID
- **POST /api/products**: Create a new product
- **PUT /api/products/{id}**: Update a product by ID
- **DELETE /api/products/{id}**: Delete a product by ID

### Transactions

- **GET /api/transactions**: Retrieve all transactions
- **GET /api/transactions/{id}**: Retrieve a transaction by ID
- **POST /api/transactions**: Create a new transaction
- **GET /api/transactions/source/{sourceAccountId}**: Retrieve transactions by source account ID
- **GET /api/transactions/destination/{destinationAccountId}**: Retrieve transactions by destination account ID

## API Authentication

The API uses OAuth2 JWT tokens for authentication. Before accessing any protected endpoint, you must first obtain an access token.

### Obtaining an Access Token

To obtain an access token, send a POST request to the /oauth2/token endpoint.

#### Endpoint
POST http://localhost:8080/oauth2/token
#### Request Headers:
- Content-Type: `application/x-www-form-urlencoded`
- Authorization: `Basic {Base64 encoded client_id:client_secret}`

#### Request Body:
grant_type=client_credentials&scope=read write

#### Example request using cURL 

```
curl --location 'http://localhost:8080/oauth2/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--header 'Authorization: Basic ZGVmYXVsdC1jbGllbnQtaWQ6ZGVmYXVsdC1zZWNyZXQ=' \
--data-urlencode 'grant_type=client_credentials' \
--data-urlencode 'scope=read write'
```

#### Response example
```
{
  "access_token": "eyJraWQiOiIxNmM3YTI5ZC1jNTU3LTRkZTUtYjZkNy1mZTI1ZDc2M2U2NTYiLCJhbGciOiJSUzI1NiJ9...",
  "scope": "read write",
  "token_type": "Bearer",
  "expires_in": 299
}
```

### Consuming Protected Endpoints

Once you have obtained the access token, you can access the protected API endpoints by including the token in the Authorization header.

#### Example
```
curl --location 'http://localhost:8080/api/clients' \
--header 'Authorization: Bearer eyJraWQiOiIxNjAzNTYzZi1iYWJkLTQ1MDU...'
```

## Script

```sql
CREATE DATABASE financial_institution;

USE financial_institution;

CREATE TABLE clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    identification_type VARCHAR(10) NOT NULL,
    identification_number VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    birthdate DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHECK (LENGTH(first_name) >= 2),
    CHECK (LENGTH(last_name) >= 2)
);

CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_type ENUM('SAVINGS', 'CHECKING') NOT NULL,
    account_number VARCHAR(10) NOT NULL UNIQUE,
    status ENUM('ACTIVE', 'INACTIVE', 'CANCELED') NOT NULL,
    balance DECIMAL(15,2) NOT NULL,
    gmf_exempt BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    client_id BIGINT,
    FOREIGN KEY (client_id) REFERENCES clients(id)
);

CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER') NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    source_account_id BIGINT,
    destination_account_id BIGINT,
    FOREIGN KEY (source_account_id) REFERENCES products(id),
    FOREIGN KEY (destination_account_id) REFERENCES products(id)
);