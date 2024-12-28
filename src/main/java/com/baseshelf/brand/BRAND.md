# Brand Module Documentation

## Overview
The **Brand Module** is designed to manage brands within the context of a store in the BaseShelf application. This module handles operations such as creating, retrieving, updating, and deleting brands, as well as associating brands with products and stores. Each brand belongs to a specific store and can have multiple products under it.

---

## Features
- **CRUD Operations**: Create, retrieve, update, and delete brands.
- **Association with Store**: Brands are associated with a store.
- **Product Management**: Brands can manage multiple products.
- **Sorting**: Retrieve brands sorted by the number of associated products.
- **Validation**: Implements constraints to ensure data integrity.
- **Custom Endpoints**: Includes endpoints for advanced queries like finding brands by product ID or getting the brand count for a store.

---

## Key Classes

### 1. Brand
#### Description:
Represents the brand entity.

#### Attributes:
| Attribute        | Type          | Description                                       |
|------------------|---------------|---------------------------------------------------|
| `description`    | `String`      | A brief description of the brand (max 500 chars).|
| `store`          | `Store`       | The store to which the brand belongs.            |
| `products`       | `List<Product>` | The list of products under the brand.            |

#### Annotations:
- **@Entity**: Marks the class as a JPA entity.
- **@Size**: Validates string lengths.
- **@JsonBackReference**: Prevents circular references during serialization.

### 2. BrandController
#### Description:
Defines REST APIs to interact with the Brand module.

#### Endpoints:
| HTTP Method | Endpoint                                     | Description                                               |
|-------------|---------------------------------------------|-----------------------------------------------------------|
| GET         | `/stores/store-id/{store-id}/brands`        | Retrieve all brands for a store.                          |
| GET         | `/stores/store-id/{store-id}/brands/sorted` | Retrieve all brands for a store sorted by product count.  |
| POST        | `/stores/store-id/{store-id}/brands`        | Create a new brand for a store.                           |
| GET         | `/stores/store-id/{store-id}/brands/brand-id/{brand-id}` | Retrieve a brand by its ID.                   |
| GET         | `/stores/store-id/{store-id}/brands/count`  | Get the count of brands in a store.                      |
| GET         | `/stores/store-id/{store-id}/brands/product-id/{product-id}` | Retrieve a brand by a product ID.          |
| PUT         | `/stores/store-id/{store-id}/brands/update/brand-id/{brand-id}` | Update a brand by its ID.                  |
| DELETE      | `/stores/store-id/{store-id}/brands/delete/brand-id/{brand-id}` | Delete a brand by its ID.                  |

### 3. BrandService
#### Description:
Handles the business logic for the Brand module.

#### Key Methods:
| Method                              | Description                                                                     |
|-------------------------------------|---------------------------------------------------------------------------------|
| `getAllBrandsByStore(Long storeId)` | Retrieves all brands associated with a specific store.                         |
| `registerBrand(Long storeId, Brand brand)` | Creates or registers a brand for a specific store.                     |
| `getBrandById(Long storeId, Long brandId)` | Retrieves a brand by its ID for a given store.                        |
| `updateBrandById(Long storeId, Long brandId, BrandDto brandDto)` | Updates brand details.                         |
| `deleteBrandById(Long storeId, Long brandId)` | Deletes a brand by its ID.                                              |
| `getBrandByProduct(Long storeId, Long productId)` | Retrieves a brand associated with a specific product.             |
| `getBrandCountByStore(Long storeId)` | Gets the total number of brands for a given store.                         |
| `sortByProductCount(Long storeId)` | Retrieves all brands for a store sorted by the number of associated products. |

### 4. BrandRepository
#### Description:
Defines database queries for the Brand module.

#### Key Methods:
| Method                                       | Description                                                                 |
|----------------------------------------------|-----------------------------------------------------------------------------|
| `findAllByStore(Store store)`                | Finds all brands belonging to a store.                                     |
| `findByStoreAndId(Store store, Long brandId)`| Finds a brand by store and ID.                                             |
| `countByStore(Store store)`                  | Counts the number of brands in a store.                                    |
| `findByStoreAndName(Store store, String name)` | Finds a brand by store and name.                                          |
| `findAllByStoreOrderByProductsDesc(Store store)` | Finds all brands in a store sorted by the number of associated products. |

### 5. BrandDto
#### Description:
Data Transfer Object for creating or updating brand data.

#### Attributes:
| Attribute     | Type     | Description                                      |
|---------------|----------|--------------------------------------------------|
| `name`        | `String` | Name of the brand (2 to 30 characters).          |
| `description` | `String` | Description of the brand (max 500 characters).   |

---

## Validations
- **Name**: Must be between 2 and 30 characters.
- **Description**: Must not exceed 500 characters.
- **Non-Null Fields**: Certain fields like `name` must not be null.

---

## Example Use Cases
1. **Create a New Brand**:
    - Endpoint: `POST /stores/store-id/{store-id}/brands`
    - Request Body:
      ```json
      {
        "name": "Nike",
        "description": "A leading brand for sportswear."
      }
      ```

2. **Get All Brands for a Store**:
    - Endpoint: `GET /stores/store-id/{store-id}/brands`
    - Response:
      ```json
      [
        {
          "id": 1,
          "name": "Nike",
          "description": "A leading brand for sportswear."
        },
        {
          "id": 2,
          "name": "Adidas",
          "description": "Another top brand for sportswear."
        }
      ]
      ```

3. **Update a Brand**:
    - Endpoint: `PUT /stores/store-id/{store-id}/brands/update/brand-id/{brand-id}`
    - Request Body:
      ```json
      {
        "name": "Updated Nike",
        "description": "Updated description for Nike."
      }
      ```

---

## CommandLineRunner
A CommandLineRunner bean is included to seed the database with sample brands. It generates 10 random brands for a specific store using the Faker library.

---

## Error Handling
- **BrandNotFoundException**: Thrown when a brand is not found by its ID or name.
- **Validation Errors**: Handled for invalid inputs like exceeding maximum string lengths or null values.

---

## Future Enhancements
- Add support for searching brands by partial names.
- Implement caching for frequently accessed data.
- Extend sorting capabilities to include alphabetical order and creation date.

---

## Conclusion
The Brand module provides robust features for managing brands within a store. It ensures data consistency and provides comprehensive APIs to interact with brands effectively.

