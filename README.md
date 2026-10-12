<div align="center">

# Furniro — Backend API

**REST API for the Furniro furniture e-commerce platform**

![Java](https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F?logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?logo=springsecurity&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-database-4169E1?logo=postgresql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-migrations-CC0200?logo=flyway&logoColor=white)
![Status](https://img.shields.io/badge/status-in%20development-yellow)

</div>

---

## Table of Contents

1. [About the Project](#about-the-project)
2. [Features](#features)
3. [Tech Stack](#tech-stack)
4. [Architecture](#architecture)
5. [Data Model](#data-model)
6. [Getting Started](#getting-started)
7. [Configuration](#configuration)
8. [API Reference](#api-reference)
   - [Conventions](#conventions)
   - [Authentication](#authentication)
   - [Endpoint Summary](#endpoint-summary)
   - [Auth Endpoints](#auth-endpoints)
   - [Product Endpoints](#product-endpoints)
   - [Cart Endpoints](#cart-endpoints)
   - [Favorite Endpoints](#favorite-endpoints)
   - [Schemas](#schemas)
   - [Errors](#errors)
9. [Frontend Integration Guide](#frontend-integration-guide)
10. [Known Issues & Limitations](#known-issues--limitations)
11. [Roadmap](#roadmap)

---

## About the Project

**Furniro** is a full-stack furniture e-commerce application. This repository contains the **backend**: a stateless REST API built with Spring Boot that supports the storefront. It provides:

- **User accounts**: registration and login with JWT-based authentication.
- **Product catalogue**: paginated, sortable, filterable product listing; product details; and "related products" recommendations.
- **Shopping cart**: a persistent cart per user. Items can be added, updated and removed, and line totals and the cart total are calculated on the server.
- **Wishlist / favorites**: users can save products to a personal favorites list.

The database schema is versioned with **Flyway**. It already covers the wider domain (categories, promotions, product images and reviews), so new features can be added on top of a stable data model.

> **Project status:** 🚧 *Active development.* The core shopping endpoints (auth, products, cart and favorites) are ready for frontend integration. Orders/checkout, categories, reviews and admin management are planned. See the [Roadmap](#roadmap).

---

## Features

| Area | Status | Description |
|---|:---:|---|
| Authentication | ✅ | Register, log in, JWT (HMAC-256) bearer tokens, stateless sessions, BCrypt password hashing |
| Role model | ✅ | `USER` and `ADMIN` roles (`ADMIN` also has `ROLE_USER`) |
| Product listing | ✅ | Pagination, sorting, filters by category, name search and price range; only in-stock products |
| Product details | ✅ | Single product with its promotion data (discount, start/end dates) |
| Related products | ✅ | Up to 4 in-stock products from the same category |
| Shopping cart | ✅ | Get-or-create cart, add item (merges with an existing line), update quantity, remove item, server-side totals |
| Favorites | ✅ | List, add (with duplicate detection) and remove favorite products |
| Database migrations | ✅ | 13 Flyway migrations, including seed data (3 categories, 8 products) |
| Categories API | 🔜 | Table and entity exist; the route is already public in the security config, but there is no controller yet |
| Product images | 🔜 | Table and entity exist; not included in API responses yet |
| Reviews | 🔜 | Table and entity exist (1–5 rating, one review per user per product) |
| Promotions management | 🔜 | Table and entity exist; promotions are returned on products but cannot be managed through the API yet |
| Orders / checkout | 🔜 | Planned |
| Admin endpoints | 🔜 | Planned (product, category and promotion CRUD) |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 25 (see [note](#prerequisites)) |
| Framework | Spring Boot 4.1 (Spring Web MVC) |
| Security | Spring Security 7, `com.auth0:java-jwt` 4.6 |
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL |
| Migrations | Flyway |
| Validation | Jakarta Bean Validation (`spring-boot-starter-validation`) |
| Boilerplate | Lombok |
| Build | Maven (wrapper included) |

---

## Architecture

The project uses a standard **layered architecture**:

```
            HTTP request
                 │
                 ▼
┌──────────────────────────────────┐
│  SecurityFilter (JWT)            │  reads the "Authorization: Bearer" header,
│                                  │  validates the token, loads the User
└──────────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────┐
│  Controllers  (/api/**)          │  HTTP mapping, request/response DTOs
└──────────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────┐
│  Services                        │  business rules, entity → DTO mapping
└──────────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────┐
│  Repositories (Spring Data JPA)  │  derived queries + JPQL
└──────────────────────────────────┘
                 │
                 ▼
           PostgreSQL (schema managed by Flyway)
```

### Project structure

```
src/main/java/com/furniro/backend
├── FurniroApplication.java          # Spring Boot entry point
├── controller/                      # REST controllers
│   ├── AutheticationController.java   → /api/auth
│   ├── ProductController.java         → /api/products
│   ├── CartController.java            → /api/cart
│   └── FavoriteController.java        → /api/favorites
├── service/                         # Business logic
│   ├── AuthorizationService.java      (UserDetailsService)
│   ├── ProductService.java
│   ├── CartService.java
│   └── FavoriteService.java
├── repository/                      # Spring Data JPA repositories
├── domain/                          # JPA entities + DTOs (records), grouped by aggregate
│   ├── users/  products/  categories/  promotions/  product_image/
│   ├── cart/   cart_item/  favorites/  reviews/
├── exception/                       # ResourceNotFoundException (404), DuplicateResourceException (409)
└── infra/security/                  # SecurityConfiguration, SecurityFilter, TokenService

src/main/resources
├── application.properties
└── db/migration/                    # Flyway migrations V1 … V13
```

---

## Data Model

```mermaid
erDiagram
    USERS ||--o| CARTS : owns
    USERS ||--o{ FAVORITES : saves
    USERS ||--o{ REVIEWS : writes
    CATEGORIES ||--o{ PRODUCTS : groups
    PROMOTIONS |o--o{ PRODUCTS : discounts
    PRODUCTS ||--o{ PRODUCT_IMAGES : has
    PRODUCTS ||--o{ REVIEWS : receives
    PRODUCTS ||--o{ CART_ITEMS : "appears in"
    PRODUCTS ||--o{ FAVORITES : "appears in"
    CARTS ||--o{ CART_ITEMS : contains

    USERS {
        bigint id PK
        varchar email UK
        varchar password "BCrypt hash"
        varchar first_name
        varchar last_name
        varchar role "USER | ADMIN"
        timestamptz created_at
    }
    CATEGORIES {
        bigint id PK
        varchar name UK
        varchar description
        varchar image_url
        timestamptz created_at
    }
    PROMOTIONS {
        bigint id PK
        varchar name
        int discount_percent
        date start_date
        date end_date "CHECK end_date >= start_date"
        timestamptz created_at
    }
    PRODUCTS {
        bigint id PK
        bigint category_id FK "ON DELETE RESTRICT"
        bigint promotion_id FK "nullable, ON DELETE SET NULL"
        varchar name
        text description
        numeric price "12,2 · >= 0"
        int stock ">= 0"
        timestamptz created_at
    }
    PRODUCT_IMAGES {
        bigint id PK
        bigint product_id FK
        varchar url
        int position
    }
    REVIEWS {
        bigint id PK
        bigint product_id FK
        bigint user_id FK
        int rating "1..5"
        text comment
        timestamptz created_at
    }
    CARTS {
        bigint id PK
        bigint user_id FK, UK "one cart per user"
        timestamptz created_at
        timestamptz updated_at
    }
    CART_ITEMS {
        bigint id PK
        bigint cart_id FK
        bigint product_id FK
        int quantity "> 0"
        timestamptz created_at
        timestamptz updated_at
    }
    FAVORITES {
        bigint id PK
        bigint user_id FK
        bigint product_id FK
        timestamptz created_at
    }
```

**Key constraints**

- One cart per user (`carts.user_id UNIQUE`).
- A product appears **at most once per cart** (`UNIQUE(cart_id, product_id)`). Adding the same product again increases the quantity.
- A product can be favorited **once per user** (`UNIQUE(user_id, product_id)`).
- One review per user per product (`UNIQUE(user_id, product_id)`).
- Deleting a user cascades to their cart, cart items, favorites and reviews. A category that still has products cannot be deleted.

### Seed data (`V9__seed-products.sql`)

| Category ID | Category | Products (id: name) |
|:---:|---|---|
| 1 | Dining | 1: Syltherine, 2: Leviosa |
| 2 | Living | 3: Lolito, 4: Respira, 7: Pingky |
| 3 | Bedroom | 5: Grifo, 6: Muggo, 8: Potty |

The seed data has no promotions or product images.

---

## Getting Started

### Prerequisites

- **JDK 25.** `pom.xml` sets `java.version` to 21, but the `maven-compiler-plugin` explicitly compiles with `<source>25</source>` / `<target>25</target>`, so a JDK 25 is required to build. Building with JDK 21 fails.
- **PostgreSQL 14+** (tested with 16).
- Maven is optional because the project includes the Maven wrapper (`./mvnw`).

### 1. Create the database

```sql
CREATE USER thove WITH PASSWORD 'password1234';
CREATE DATABASE furniro OWNER thove;
```

> These credentials match the defaults in `application.properties`. Use your own values and override them as shown in [Configuration](#configuration).

### 2. Run the application

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

When the app starts, Flyway applies all migrations (`V1` → `V13`), including the seed data. The API is then available at:

```
http://localhost:8080
```

### 3. Quick check

```bash
curl "http://localhost:8080/api/products?size=3"
```

### Build a JAR / run tests

```bash
./mvnw clean package          # builds target/furniro-0.0.1-SNAPSHOT.jar
java -jar target/furniro-0.0.1-SNAPSHOT.jar

./mvnw test                   # needs a running PostgreSQL (the context-load test connects to the DB)
```

---

## Configuration

All settings are in `src/main/resources/application.properties`. You can override any of them with environment variables (Spring relaxed binding) or `--key=value` arguments.

| Property | Env variable | Default | Description |
|---|---|---|---|
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/furniro` | JDBC URL |
| `spring.datasource.username` | `SPRING_DATASOURCE_USERNAME` | `thove` | DB user |
| `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` | `password1234` | DB password |
| `api.security.token.secret` | `JWT_SECRET` | `my-secret-key` | HMAC-256 secret used to sign JWTs. **Always set this outside local development.** |
| `spring.jpa.hibernate.ddl-auto` | — | `none` | Hibernate never changes the schema; Flyway owns it |
| `spring.flyway.baseline-on-migrate` | — | `true` | Allows Flyway to start on an existing, non-empty schema |
| `server.port` | `SERVER_PORT` | `8080` | HTTP port |

Example:

```bash
export SPRING_DATASOURCE_USERNAME=furniro
export SPRING_DATASOURCE_PASSWORD=supersecret
export JWT_SECRET="a-long-random-secret-of-at-least-32-chars"
./mvnw spring-boot:run
```

---

## API Reference

### Conventions

| Item | Value |
|---|---|
| Base URL | `http://localhost:8080` |
| Path prefix | All routes start with `/api` |
| Content type | `application/json` (requests and responses) |
| Auth scheme | `Authorization: Bearer <JWT>` |
| IDs | 64-bit integers (`number` in JSON) |
| Money | Decimal numbers with 2 decimal places, e.g. `2500000.00`. No currency field; the seed data follows the Furniro design (Rp). |
| Dates | Promotion dates use `YYYY-MM-DD`; timestamps are ISO-8601 UTC (`2026-10-12T21:17:38.595542Z`) |
| Field naming | camelCase, **with two exceptions**: `linetotal` (cart items) and `FavoritedAt` (favorites) |

### Authentication

The API is **stateless** and uses **JSON Web Tokens**.

1. Call `POST /api/auth/login` with an email and password to get a `token`.
2. Send the token in the `Authorization` header of every protected request:
   ```http
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

**Token details**

| Claim | Value |
|---|---|
| Algorithm | `HS256` |
| `iss` | `furniro` |
| `sub` | the user's **email** |
| `exp` | issue time + 2 hours (see [Known Issues](#known-issues--limitations) #2) |

There is no refresh-token or `/me` endpoint yet. To show the logged-in user's email, decode the token's `sub` claim on the client. When a token is invalid or expired, the request is treated as anonymous: public routes still work, and protected routes return **`403`**.

**Access rules** (from `SecurityConfiguration`)

| Rule | Routes |
|---|---|
| 🌐 Public | `POST /api/auth/login`, `POST /api/auth/register`, `GET /api/products/**`, `GET /api/categories/**` |
| 🔒 Authenticated | Everything else (`/api/cart/**`, `/api/favorites/**`, …) |

### Endpoint Summary

| Method | Endpoint | Auth | Description | Success |
|---|---|:---:|---|:---:|
| `POST` | [`/api/auth/register`](#post-apiauthregister) | 🌐 | Create a user account | `200` |
| `POST` | [`/api/auth/login`](#post-apiauthlogin) | 🌐 | Log in and get a JWT | `200` |
| `GET` | [`/api/products`](#get-apiproducts) | 🌐 | Paginated, filterable product list | `200` |
| `GET` | [`/api/products/{id}`](#get-apiproductsid) | 🌐 | Product details | `200` |
| `GET` | [`/api/products/{id}/related`](#get-apiproductsidrelated) | 🌐 | Up to 4 related products | `200` |
| `GET` | [`/api/cart`](#get-apicart) | 🔒 | Get the current user's cart (created on first access) | `200` |
| `POST` | [`/api/cart/items`](#post-apicartitems) | 🔒 | Add a product to the cart | `200` |
| `PATCH` | [`/api/cart/items/{itemId}`](#patch-apicartitemsitemid) | 🔒 | Set a cart item's quantity | `200` |
| `DELETE` | [`/api/cart/items/{itemId}`](#delete-apicartitemsitemid) | 🔒 | Remove an item from the cart ⚠️ | `200` |
| `GET` | [`/api/favorites`](#get-apifavorites) | 🔒 | List the current user's favorites | `200` |
| `POST` | [`/api/favorites`](#post-apifavorites) | 🔒 | Add a product to favorites | `201` |
| `DELETE` | [`/api/favorites/{productId}`](#delete-apifavoritesproductid) | 🔒 | Remove a product from favorites | `204` |

> ⚠️ **Read [Errors](#errors) before integrating.** At the moment every error response reaches the client as **`403 Forbidden` with an empty body**, whatever the real cause.

---

### Auth Endpoints

#### `POST /api/auth/register`

Creates a new user account. The password is hashed with BCrypt before it is stored.

**Request body**

| Field | Type | Required | Notes |
|---|---|:---:|---|
| `firstname` | string | – | Note the lowercase `n` |
| `lastname` | string | – | Note the lowercase `n` |
| `email` | string | ✅ | Must be unique; used as the login username |
| `password` | string | ✅ | Plain text; hashed on the server |
| `role` | `"USER"` \| `"ADMIN"` | ✅ | Must be sent. If it is missing, the request fails |

```json
{
  "firstname": "Ana",
  "lastname": "Silva",
  "email": "ana@example.com",
  "password": "secret123",
  "role": "USER"
}
```

**Responses**

| Status | Body | When |
|---|---|---|
| `200 OK` | *empty* | Account created. Call `/login` next; registering does not return a token. |
| `400 Bad Request` | *empty* | The email is already registered |
| `403 Forbidden` | *empty* | Other failures, such as a missing `role` (see [Errors](#errors)) |

---

#### `POST /api/auth/login`

Authenticates a user and returns a JWT.

**Request body**

```json
{
  "email": "ana@example.com",
  "password": "secret123"
}
```

**Responses**

| Status | Body | When |
|---|---|---|
| `200 OK` | `LoginResponse` | Valid credentials |
| `403 Forbidden` | *empty* | Wrong email or password |

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJmdXJuaXJvIiwic3ViIjoiYW5hQGV4YW1wbGUuY29tIiwiZXhwIjoxNzkxODQ3MDU4fQ.…"
}
```

---

### Product Endpoints

#### `GET /api/products`

Returns a **paginated** list of products that are **in stock** (`stock > 0`).

**Query parameters**

| Param | Type | Default | Description |
|---|---|---|---|
| `page` | int | `0` | Zero-based page index |
| `size` | int | `20` | Page size (max `2000`) |
| `sort` | string | unsorted | `property[,asc\|desc]`. Repeat it to sort by several fields. Sortable properties: `id`, `name`, `price`, `stock`, `createdAt` |
| `categoryId` | long | – | Only return products in this category (seeded: `1` Dining, `2` Living, `3` Bedroom) |
| `search` | string | – | **Case-insensitive** substring match on the product **name only** (not the description) |
| `minPrice` | decimal | – | Price ≥ `minPrice` |
| `maxPrice` | decimal | – | Price ≤ `maxPrice` |

All filters are optional and can be combined (AND logic).

**Examples**

```http
GET /api/products?page=0&size=8&sort=price,desc
GET /api/products?categoryId=2&minPrice=100000&maxPrice=5000000
GET /api/products?search=lol&sort=name,asc
```

**Response `200 OK`**: `Page<Product>`

```json
{
  "content": [
    {
      "id": 7,
      "name": "Pingky",
      "description": "Cute bed set",
      "price": 7000000.00,
      "stock": 3,
      "discount": null,
      "startDate": null,
      "endDate": null
    },
    {
      "id": 3,
      "name": "Lolito",
      "description": "Luxury big sofa",
      "price": 7000000.00,
      "stock": 5,
      "discount": null,
      "startDate": null,
      "endDate": null
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 2,
    "offset": 0,
    "paged": true,
    "unpaged": false,
    "sort": { "empty": false, "sorted": true, "unsorted": false }
  },
  "totalElements": 8,
  "totalPages": 4,
  "number": 0,
  "size": 2,
  "numberOfElements": 2,
  "first": true,
  "last": false,
  "empty": false,
  "sort": { "empty": false, "sorted": true, "unsorted": false }
}
```

> For pagination UI you mainly need: `content`, `number` (current page), `totalPages`, `totalElements`, `first` and `last`.
> A page index past the end returns `200` with an empty `content` array. An unknown `sort` property causes an error.

---

#### `GET /api/products/{id}`

Returns one product. Unlike the list endpoint, this also returns products that are **out of stock**, so check `stock` before you enable "Add to cart".

| Path param | Type | Description |
|---|---|---|
| `id` | long | Product ID |

**Response `200 OK`**: `Product`

```json
{
  "id": 3,
  "name": "Lolito",
  "description": "Luxury big sofa",
  "price": 7000000.00,
  "stock": 5,
  "discount": null,
  "startDate": null,
  "endDate": null
}
```

| Status | When |
|---|---|
| `200 OK` | Product found |
| `403 Forbidden` *(should be 404)* | No product with that ID, or the ID is not a number |

---

#### `GET /api/products/{id}/related`

Returns **up to 4** in-stock products from the **same category**, excluding the product itself. The response is a plain array, not a page.

**Response `200 OK`**: `Product[]`

```json
[
  {
    "id": 2,
    "name": "Leviosa",
    "description": "Stylish cafe chair",
    "price": 2500000.00,
    "stock": 8,
    "discount": null,
    "startDate": null,
    "endDate": null
  }
]
```

| Status | When |
|---|---|
| `200 OK` | Product found (the array may be empty) |
| `403 Forbidden` *(should be 404)* | The source product does not exist |

---

### Cart Endpoints

🔒 All cart endpoints need a valid token. Each user has **one cart**, which is **created automatically** the first time it is accessed. **All cart endpoints return the full updated cart**, so the frontend can replace its cart state with the response.

Totals are calculated on the server: `lineTotal = unitPrice × quantity` and `total = Σ lineTotal`. Prices use the product's **base price**; promotions are not applied yet.

#### `GET /api/cart`

**Response `200 OK`**: `Cart`

```json
{
  "cartId": 1,
  "items": [
    {
      "itemId": 1,
      "productId": 3,
      "productName": "Lolito",
      "unitPrice": 7000000.00,
      "quantity": 2,
      "linetotal": 14000000.00
    }
  ],
  "total": 14000000.00
}
```

Empty cart:

```json
{ "cartId": 1, "items": [], "total": 0 }
```

---

#### `POST /api/cart/items`

Adds a product to the cart. If the product is **already in the cart**, the given `quantity` is **added** to the existing line, so no second line is created.

**Request body**

| Field | Type | Required | Notes |
|---|---|:---:|---|
| `productId` | long | ✅ | Must be an existing product |
| `quantity` | int | ✅ | Must be > 0. **Send a positive number:** this is not validated yet, and a negative value lowers the quantity |

```json
{ "productId": 3, "quantity": 2 }
```

**Responses**

| Status | Body | When |
|---|---|---|
| `200 OK` | `Cart` | Item added or quantity increased |
| `403 Forbidden` *(should be 404)* | *empty* | The product does not exist |
| `403 Forbidden` | *empty* | Missing or invalid token |

> Stock is **not** checked yet, so you can add more units than `stock`. Limit the quantity selector on the frontend.

---

#### `PATCH /api/cart/items/{itemId}`

**Sets** (replaces) the quantity of a cart line.

| Path param | Type | Description |
|---|---|---|
| `itemId` | long | The cart **item** ID (`items[].itemId`), **not** the product ID |

**Request body**

```json
{ "quantity": 4 }
```

**Responses**

| Status | Body | When |
|---|---|---|
| `200 OK` | `Cart` | Quantity updated |
| `403 Forbidden` *(should be 400)* | *empty* | `quantity` is missing, `0` or negative |
| `403 Forbidden` *(should be 404)* | *empty* | The item does not exist or belongs to another user's cart |

---

#### `DELETE /api/cart/items/{itemId}`

Removes a line from the cart.

| Path param | Type | Description |
|---|---|---|
| `itemId` | long | The cart **item** ID (`items[].itemId`) |

**Responses**

| Status | Body | When |
|---|---|---|
| `200 OK` | `Cart` | Request accepted (see warning below) |
| `403 Forbidden` *(should be 404)* | *empty* | The item does not exist or belongs to another user's cart |

> ⚠️ **Known bug:** this endpoint currently returns `200`, but **the item is not deleted**; it is still in the returned cart and in the database. See [Known Issues](#known-issues--limitations) #1.

---

### Favorite Endpoints

🔒 All favorite endpoints need a valid token.

#### `GET /api/favorites`

Returns the current user's favorite products.

**Response `200 OK`**: `Favorite[]`

```json
[
  {
    "id": 5,
    "name": "Grifo",
    "description": "Night lamp",
    "price": 1500000.00,
    "stock": 20,
    "discount": null,
    "startDate": null,
    "endDate": null,
    "FavoritedAt": "2026-10-12T21:17:38.595542Z"
  }
]
```

> `id` is the **product ID**, not a favorite-record ID. Use it for `DELETE /api/favorites/{productId}` and to link to the product page.
> `FavoritedAt` starts with an uppercase `F`.

---

#### `POST /api/favorites`

Adds a product to the current user's favorites.

**Request body**

```json
{ "productId": 5 }
```

**Responses**

| Status | Body | When |
|---|---|---|
| `201 Created` | *empty* | Favorite added. Call `GET /api/favorites` again if you need the updated list |
| `403 Forbidden` *(should be 409)* | *empty* | The product is already in favorites |
| `403 Forbidden` *(should be 404)* | *empty* | The product does not exist |

---

#### `DELETE /api/favorites/{productId}`

Removes a product from favorites. This is **idempotent**: it returns `204` even if the product was not in favorites.

| Path param | Type | Description |
|---|---|---|
| `productId` | long | The **product** ID |

**Responses**

| Status | Body | When |
|---|---|---|
| `204 No Content` | *empty* | Removed, or it was not a favorite |

---

### Schemas

TypeScript definitions that you can copy into the frontend:

```ts
// ---------- Auth ----------
export type UserRole = "USER" | "ADMIN";

export interface RegisterRequest {
  firstname?: string;
  lastname?: string;
  email: string;
  password: string;
  role: UserRole;            // required — send "USER"
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}

// ---------- Products ----------
export interface Product {
  id: number;
  name: string;
  description: string | null;
  price: number;             // base price, 2 decimals
  stock: number;
  discount: number | null;   // promotion discount in percent (e.g. 30 = 30%)
  startDate: string | null;  // "YYYY-MM-DD" — promotion start
  endDate: string | null;    // "YYYY-MM-DD" — promotion end
}

export interface Page<T> {
  content: T[];
  number: number;            // current page (0-based)
  size: number;
  numberOfElements: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface ProductQuery {
  page?: number;
  size?: number;
  sort?: string | string[];  // e.g. "price,asc"
  categoryId?: number;
  search?: string;
  minPrice?: number;
  maxPrice?: number;
}

// ---------- Cart ----------
export interface CartItem {
  itemId: number;            // use for PATCH / DELETE
  productId: number;
  productName: string;
  unitPrice: number;
  quantity: number;
  linetotal: number;         // lowercase "t"
}

export interface Cart {
  cartId: number;
  items: CartItem[];
  total: number;
}

export interface AddCartItemRequest {
  productId: number;
  quantity: number;          // > 0
}

export interface UpdateQuantityRequest {
  quantity: number;          // > 0
}

// ---------- Favorites ----------
export interface Favorite extends Product {
  FavoritedAt: string;       // ISO-8601 timestamp, uppercase "F"
}

export interface FavoriteRequest {
  productId: number;
}
```

#### Promotions and prices

`discount`, `startDate` and `endDate` come from the product's promotion. They are `null` when the product has no promotion. **The API does not check whether a promotion is active, and it does not apply the discount to `price`.** To show a sale price, check the dates on the client:

```ts
export function effectivePrice(p: Product, today = new Date().toISOString().slice(0, 10)): number {
  const active =
    p.discount != null && p.startDate != null && p.endDate != null &&
    p.startDate <= today && today <= p.endDate;
  return active ? +(p.price * (1 - p.discount! / 100)).toFixed(2) : p.price;
}
```

> The cart always uses the base `price`, so the cart total does not include discounts yet.

---

### Errors

The domain exceptions are mapped to the correct status codes (`ResourceNotFoundException` → `404`, `DuplicateResourceException` → `409`). However, Spring forwards errors to the internal `/error` endpoint, and **`/error` is not in the security config's `permitAll` list**. So, for now, **every error reaches the client as `403 Forbidden` with an empty body**:

| Situation | Intended status | **Actual status today** |
|---|:---:|:---:|
| Missing / invalid / expired token on a protected route | `401` | `403` |
| Wrong login credentials | `401` | `403` |
| Resource not found (product, cart item) | `404` | `403` |
| Already favorited | `409` | `403` |
| Invalid input (bad quantity, non-numeric ID, unknown sort field) | `400` | `403` |
| Unexpected server error | `500` | `403` |
| Email already registered | `400` | `400` ✅ *(returned directly by the controller)* |

**Frontend recommendation:** until this is fixed, decode the token's `exp` claim. On a `403` from a protected route with an expired (or missing) token, log the user out and redirect to login; otherwise show a generic error. On a public route, treat `403` as "not found / invalid request". Error bodies are empty, so do not try to parse them.

> **Backend fix (planned):** add `.requestMatchers("/error").permitAll()` to the security chain and add a `@RestControllerAdvice` that returns a consistent JSON error body, e.g.
> `{ "status": 404, "error": "Not Found", "message": "Product Not Found with id: 99", "path": "/api/products/99", "timestamp": "…" }`.

---

## Frontend Integration Guide

### 1. CORS: use a dev proxy

**CORS is not configured on the backend yet.** Browsers will block calls from a frontend on another origin (e.g. `http://localhost:5173` → `http://localhost:8080`). Preflight `OPTIONS` requests currently return `403`.

Until CORS is added on the backend, route API calls through your dev server's proxy. With **Vite**:

```ts
// vite.config.ts
export default defineConfig({
  server: {
    proxy: {
      "/api": { target: "http://localhost:8080", changeOrigin: true },
    },
  },
});
```

Then call relative URLs such as `fetch("/api/products")`.

### 2. A minimal API client

```ts
const BASE = "/api";

function authHeaders(): HeadersInit {
  const token = localStorage.getItem("token");
  return token ? { Authorization: `Bearer ${token}` } : {};
}

async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const res = await fetch(`${BASE}${path}`, {
    ...init,
    headers: { "Content-Type": "application/json", ...authHeaders(), ...init.headers },
  });
  if (!res.ok) throw new ApiError(res.status, path);
  // 201 / 204 and some 200 responses have empty bodies
  const text = await res.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

export class ApiError extends Error {
  constructor(public status: number, public path: string) {
    super(`Request to ${path} failed with ${status}`);
  }
}

export const api = {
  // auth
  register: (b: RegisterRequest) => request<void>("/auth/register", { method: "POST", body: JSON.stringify(b) }),
  login: (b: LoginRequest) => request<LoginResponse>("/auth/login", { method: "POST", body: JSON.stringify(b) }),

  // products
  products: (q: ProductQuery = {}) => {
    const params = new URLSearchParams();
    Object.entries(q).forEach(([k, v]) =>
      v != null && ([] as unknown[]).concat(v).forEach((x) => params.append(k, String(x))));
    return request<Page<Product>>(`/products?${params}`);
  },
  product: (id: number) => request<Product>(`/products/${id}`),
  related: (id: number) => request<Product[]>(`/products/${id}/related`),

  // cart
  cart: () => request<Cart>("/cart"),
  addToCart: (b: AddCartItemRequest) => request<Cart>("/cart/items", { method: "POST", body: JSON.stringify(b) }),
  updateCartItem: (itemId: number, quantity: number) =>
    request<Cart>(`/cart/items/${itemId}`, { method: "PATCH", body: JSON.stringify({ quantity }) }),
  removeCartItem: (itemId: number) => request<Cart>(`/cart/items/${itemId}`, { method: "DELETE" }),

  // favorites
  favorites: () => request<Favorite[]>("/favorites"),
  addFavorite: (productId: number) => request<void>("/favorites", { method: "POST", body: JSON.stringify({ productId }) }),
  removeFavorite: (productId: number) => request<void>(`/favorites/${productId}`, { method: "DELETE" }),
};
```

### 3. Typical flows

| Screen | Calls |
|---|---|
| Shop / listing | `GET /api/products?page=&size=&sort=&categoryId=&search=&minPrice=&maxPrice=` |
| Product page | `GET /api/products/{id}` + `GET /api/products/{id}/related` |
| Sign up | `POST /api/auth/register` → `POST /api/auth/login` → store `token` |
| Log in | `POST /api/auth/login` → store `token` |
| Header cart badge | `GET /api/cart` → `items.length` or the sum of `quantity` |
| Add to cart | `POST /api/cart/items` → replace cart state with the response |
| Cart page | `GET /api/cart`, `PATCH /api/cart/items/{itemId}`, `DELETE /api/cart/items/{itemId}` |
| Heart / wishlist toggle | `POST /api/favorites` or `DELETE /api/favorites/{productId}` |
| Wishlist page | `GET /api/favorites` |

### Quick test with cURL

```bash
# register + login
curl -X POST localhost:8080/api/auth/register -H "Content-Type: application/json" \
  -d '{"firstname":"Ana","lastname":"Silva","email":"ana@example.com","password":"secret123","role":"USER"}'

TOKEN=$(curl -s -X POST localhost:8080/api/auth/login -H "Content-Type: application/json" \
  -d '{"email":"ana@example.com","password":"secret123"}' | sed 's/.*"token":"\([^"]*\)".*/\1/')

# cart
curl -X POST localhost:8080/api/cart/items -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" -d '{"productId":3,"quantity":2}'
curl localhost:8080/api/cart -H "Authorization: Bearer $TOKEN"

# favorites
curl -X POST localhost:8080/api/favorites -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" -d '{"productId":5}'
curl localhost:8080/api/favorites -H "Authorization: Bearer $TOKEN"
```

---

## Known Issues & Limitations

These were found by running the API against PostgreSQL 16 and testing every endpoint. They are listed so the frontend can work around them, and they double as the backend to-do list.

| # | Severity | Issue | Details / suggested fix |
|:---:|:---:|---|---|
| 1 | 🔴 High | **`DELETE /api/cart/items/{itemId}` does not delete** | It returns `200`, but the row stays in the DB. The parent `Cart.items` collection (`cascade = ALL`, `orphanRemoval = true`) still references the item, so Hibernate cancels the delete on flush. Fix: `item.getCart().getItems().remove(item);` instead of `cartItemRepository.delete(item)` in `CartService.removeItem`. |
| 2 | 🔴 High | **JWT expiry depends on the server's time zone** | `TokenService.genExpirationDate()` treats the local time as `+02:00`. On a UTC+2 machine tokens last 2h; **on a UTC server (most cloud hosts), tokens expire as soon as they are issued**. Fix: `Instant.now().plus(2, ChronoUnit.HOURS)`. |
| 3 | 🔴 High | **All errors become `403` with an empty body** | `/error` is not permitted in `SecurityConfiguration`. Fix: permit `/error` and add a global `@RestControllerAdvice` (see [Errors](#errors)). |
| 4 | 🔴 High | **Anyone can register as `ADMIN`** | `role` is taken from the request body. Fix: drop `role` from `RegisterDTO` and always assign `USER`. |
| 5 | 🟠 Medium | **No CORS configuration** | Browser requests from another origin are blocked. Fix: add a `CorsConfigurationSource` bean and `.cors(...)` to the security chain. |
| 6 | 🟠 Medium | **No input validation** | `@Valid` is used without constraints on `RegisterDTO`/`AutheticationDTO`, and is missing on the cart/favorite controllers. Empty email/password is accepted; negative quantities are accepted on `POST /cart/items`. Fix: add `@NotBlank`, `@Email`, `@NotNull`, `@Positive` and `@Valid`. |
| 7 | 🟠 Medium | **Stock not enforced in the cart** | You can add more units than `stock`. |
| 8 | 🟡 Low | **Promotions are not applied to prices** | The discount is returned but not applied to `price` or to cart totals, and promotion dates are not checked. |
| 9 | 🟡 Low | **Unstable `Page` JSON format** | Spring Data warns that serializing `PageImpl` directly gives no format guarantee. Fix: `@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)`. ⚠️ This changes the response shape to `{ content, page: { size, number, totalElements, totalPages } }`, so update the frontend at the same time. |
| 10 | 🟡 Low | **Inconsistent field names** | `linetotal` → `lineTotal`, `FavoritedAt` → `favoritedAt`, `firstname`/`lastname` → `firstName`/`lastName`. These are breaking changes for the frontend. |
| 11 | 🟡 Low | **Product responses lack images and category** | `ProductResponseDTO` has no `images`, `categoryId` or `categoryName`, and no average rating. |
| 12 | 🟡 Low | **Build/config housekeeping** | The compiler targets Java 25 while `java.version` is 21. DB credentials and the JWT fallback secret are committed in `application.properties`. `spring.jpa.open-in-view` is enabled by default. |

---

## Roadmap

- [x] JWT authentication (register / login)
- [x] Product catalogue with pagination, sorting and filters
- [x] Related products
- [x] Shopping cart (get / add / update / remove)
- [x] Favorites (list / add / remove)
- [ ] Fix known issues #1–#6 (cart delete, token expiry, error handling, admin registration, CORS, validation)
- [ ] **Orders & checkout:** create an order from the cart, order history, order details, order status
- [ ] **Categories API:** `GET /api/categories`, `GET /api/categories/{id}`
- [ ] **Product images** in product responses
- [ ] **Reviews:** list/create reviews per product, average rating
- [ ] **Promotions:** apply active discounts to product and cart prices
- [ ] **User profile:** `GET /api/users/me`, update profile
- [ ] **Admin endpoints** (`ROLE_ADMIN`): CRUD for products, categories, promotions and images
- [ ] Global error handling with a consistent JSON error body
- [ ] OpenAPI / Swagger UI (`springdoc-openapi`)
- [ ] Integration tests (Testcontainers + PostgreSQL)
- [ ] Docker / docker-compose for local setup

---

<div align="center">

Built with ☕ and Spring Boot

</div>
