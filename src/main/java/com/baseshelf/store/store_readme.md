# Store Module Documentation

## Overview
The **Store** module is a core component of the BaseShelf application, designed to manage all aspects of a store, including its products, categories, brands, and orders. This module consists of entities, services, controllers, and repositories that collectively provide the functionality required to handle store-specific operations.

---

## Features
- CRUD operations for stores.
- Validation for store attributes (e.g., email, password, GSTIN).
- Lazy loading of related entities (e.g., products, brands, orders, categories).
- Disabling a store without deleting it.
- Preloading test data for development environments.

---

## Package Structure
```
com.baseshelf.store
├── Store.java              // Entity class for Store
├── StoreDto.java           // DTO for Store updates
├── StoreController.java    // REST controller for Store operations
├── StoreService.java       // Service layer for business logic
└── StoreRepository.java    // Repository interface for database operations
```

---

## Entity: `Store`
The `Store` entity represents a physical or online store.

### Attributes
| Attribute        | Type            | Constraints                                                                                  | Description                          |
|------------------|-----------------|---------------------------------------------------------------------------------------------|--------------------------------------|
| `email`          | String          | Must be a valid email, not null.                                                            | Store's unique email.                |
| `password`       | String          | Must be between 8-30 characters, not null.                                                  | Store's login password.              |
| `description`    | String          | Max length: 500 characters.                                                                 | Store description.                   |
| `address`        | String          | Max length: 50 characters.                                                                  | Store address.                       |
| `gstinNumber`    | String          | Must be exactly 15 characters.                                                              | GSTIN of the store.                  |
| `products`       | List<Product>   | Lazy-loaded.                                                                                 | List of products associated.         |
| `brands`         | List<Brand>     | Lazy-loaded.                                                                                 | List of brands associated.           |
| `productOrders`  | List<ProductOrder> | Lazy-loaded.                                                                               | List of orders associated.           |
| `categories`     | List<Category>  | Lazy-loaded.                                                                                 | List of categories associated.       |

### Relationships
- **One-to-Many**: The `Store` entity has one-to-many relationships with `Product`, `Brand`, `ProductOrder`, and `Category` entities.
- Lazy loading is used to optimize performance.

### Annotations
- **Validation**: Email, password, GSTIN, and other fields are validated using Hibernate Validator annotations.
- **Serialization**: Fields like `password` are excluded from JSON responses (`JsonProperty`), and circular references are managed using `JsonManagedReference` and `JsonIgnore`.

---

## Service: `StoreService`
The `StoreService` class encapsulates business logic for store operations.

### Methods
| Method                           | Description                                                                                           |
|----------------------------------|-------------------------------------------------------------------------------------------------------|
| `getById(Long id)`               | Fetches a store by its ID. Throws `StoreNotFoundException` if the store doesn't exist.                |
| `getAllStores()`                 | Returns a list of all stores.                                                                         |
| `getStoreCounts()`               | Returns the total number of stores.                                                                  |
| `registerStore(Store store)`     | Registers a new store, ensuring the email is unique. Assigns global categories to the store.          |
| `disableStore(Long id)`          | Disables a store by setting its `active` flag to false and updating the last modified date.           |
| `deleteStore(Long id)`           | Deletes a store by its ID.                                                                            |
| `updateStoreById(Long id, StoreDto storeDto)` | Updates store details (name and description) by its ID.                                               |
| `getByEmail(String email)`       | Fetches a store by its email. Throws `StoreNotFoundException` if the store doesn't exist.             |
| `insertStores()`                 | CommandLineRunner method to insert test data into the database during application startup.            |

### Key Functionalities
- **Validation**: Ensures unique email constraints when registering a store. Throws `MethodArgumentNotValidException` for invalid inputs.
- **Transactional Methods**: Annotated with `@Transactional` for methods like `disableStore` and `updateStoreById` to ensure atomic operations.

---

## Controller: `StoreController`
The `StoreController` class provides REST APIs for managing stores.

### Endpoints
| HTTP Method | URL                          | Description                                                             |
|-------------|------------------------------|-------------------------------------------------------------------------|
| `GET`       | `/stores`                   | Fetches all stores.                                                    |
| `GET`       | `/stores/id/{id}`           | Fetches a store by ID. Throws `StoreNotFoundException` if not found.   |
| `GET`       | `/stores/count`             | Returns the total number of stores.                                    |
| `POST`      | `/stores/registers`         | Registers a new store. Validates email uniqueness and input constraints.|
| `PUT`       | `/stores/disable/id/{id}`   | Disables a store by its ID.                                            |
| `DELETE`    | `/stores/delete/id/{id}`    | Deletes a store by its ID.                                             |
| `PUT`       | `/stores/update/id/{id}`    | Updates a store's name and description using `StoreDto`.               |

### Request and Response Examples
#### 1. Registering a Store
**Request:**
```json
{
  "email": "example@store.com",
  "password": "strongPassword",
  "description": "This is a sample store.",
  "address": "New York",
  "gstinNumber": "1234567890ABCDE"
}
```
**Response:**
```json
{
  "id": 1,
  "email": "example@store.com",
  "description": "This is a sample store.",
  "address": "New York",
  "gstinNumber": "1234567890ABCDE"
}
```

#### 2. Fetching All Stores
**Request:**
```http
GET /stores
```
**Response:**
```json
[
  {
    "id": 1,
    "email": "example@store.com",
    "description": "This is a sample store.",
    "address": "New York",
    "gstinNumber": "1234567890ABCDE"
  }
]
```

---

## DTO: `StoreDto`
The `StoreDto` class is used for updating store details.

### Attributes
| Attribute     | Type   | Constraints                                        | Description                     |
|---------------|--------|---------------------------------------------------|---------------------------------|
| `name`        | String | Must be between 2-30 characters, not null.        | Updated name of the store.      |
| `description` | String | Max length: 500 characters.                       | Updated description of the store.|

---

## Repository: `StoreRepository`
The `StoreRepository` interface extends `JpaRepository` to provide database access for the `Store` entity.

### Custom Methods
| Method                   | Description                                         |
|--------------------------|-----------------------------------------------------|
| `getStoreByEmail(String email)` | Fetches a store by its email address. Returns `Optional<Store>`. |

---

## Validation
The `Store` entity and its related DTO are validated using annotations:
- **Email Validation**: Ensures the email is in a valid format.
- **Password Validation**: Enforces minimum and maximum character limits.
- **Field Length Constraints**: Ensures attributes like `description` and `GSTIN` meet size requirements.

---

## Development Notes
- **Test Data**: The `insertStores` method in `StoreService` preloads two sample stores for development.
- **Lazy Loading**: Related entities (e.g., `Product`, `Brand`) are lazy-loaded to improve performance. Use `@JsonIgnore` to prevent circular dependencies during serialization.
- **Exception Handling**: Custom exceptions like `StoreNotFoundException` are thrown for invalid operations.

---

## Future Enhancements
- Implement authentication and authorization for store operations.
- Add support for store analytics and reporting.
- Enable filtering and sorting of stores in API responses.

