# Category Module Documentation

## Overview
The **Category** module is a core component of the Baseshelf application. It provides functionality to manage categories, which are linked to stores and products. Categories can represent attributes such as color, material, size, and product type. This module ensures effective management of categories through CRUD operations and advanced filtering.

---

## Entity: `Category`
### Fields
- **`id`**
    - Type: `Long`
    - Primary key, auto-generated using a sequence generator with an allocation size of 75.

- **`name`**
    - Type: `String`
    - Constraints: Must be between 1 and 30 characters. Cannot be `null`.

- **`categoryType`**
    - Type: `String`
    - Represents the type of category (e.g., `COLOR`, `MATERIAL`, etc.).
    - Constraints: Must be between 2 and 30 characters. Cannot be `null`.

- **`products`**
    - Type: `List<Product>`
    - Many-to-Many relationship with `Product`.
    - Annotations:
        - `@ManyToMany(mappedBy = "categories")`
        - `@JsonBackReference`
        - `@JsonProperty(access = JsonProperty.Access.READ_ONLY)`

- **`store`**
    - Type: `Store`
    - Many-to-One relationship with `Store`.
    - Annotations:
        - `@ManyToOne(fetch = FetchType.EAGER)`
        - `@JoinColumn(name = "store_id")`
        - `@JsonBackReference`
        - `@JsonProperty(access = JsonProperty.Access.READ_ONLY)`
    - Constraints: Cannot be `null`.

---

## Repository: `CategoryJpaRepository`
### Key Methods
- `Optional<Category> findByIdAndStore(Long categoryId, Store store);`
- `Optional<Category> findByStoreAndNameAndCategoryType(Store store, String name, String categoryType);`
- `List<Category> findAllByStore(Store store);`
- `List<Category> findAllByStoreAndIdIn(Store store, List<Long> ids);`

---

## Service: `CategoryService`
### Dependencies
- **`CategoryJpaRepository`**: For interacting with the database.
- **`StoreService`**: For retrieving `Store` entities.
- **`ProductService`**: For retrieving `Product` entities.

### Key Methods

#### 1. **`saveGlobalCategoriesForStore(Store store)`**
- Inserts predefined categories (`COLOR`, `MATERIAL`, `PRODUCT TYPE`, and `SIZE`) for a given store using the `Faker` library and custom lists.

#### 2. **`saveCategory(Long storeId, Category paramCategory)`**
- Saves a new category to the store if it doesn't already exist. Returns the existing category if found.

#### 3. **`getAllByIdOrNameOrCategoryType(Long storeId, Long categoryId, String paramName, String paramCategoryType)`**
- Retrieves categories filtered by ID, name, or type using dynamic specifications.

#### 4. **`getAllByStoreAndIdIn(Long storeId, List<Long> ids)`**
- Fetches categories for a store by a list of IDs.

#### 5. **`getByCategoryId(Long storeId, Long categoryId)`**
- Retrieves a category by its ID within a specific store.

#### 6. **`getAllCategoriesByNameOrCategoryType(Long storeId, String name, String categoryType)`**
- Retrieves all categories filtered by name or type.

#### 7. **`getAllCategoriesByProduct(Long storeId, Long productId)`**
- Retrieves categories associated with a specific product.

#### 8. **`deleteByIdOrNameOrCategoryType(Long storeId, Long categoryId, String name, String categoryType)`**
- Deletes categories based on filters (ID, name, or type).

#### 9. **`updateCategory(Long storeId, Long id, Category paramCategory)`**
- Updates an existing category. Throws an exception if a category with the same properties already exists.

#### 10. **`dynamicFilterByParams(Long storeId, Long categoryId, String name, String categoryType)`**
- Provides dynamic filtering using `Specification`.

#### 11. **`getAllByStore(Long storeId)`**
- Retrieves all categories for a specific store.

---

## Controller: `CategoryController`
### Endpoints

#### 1. **`GET /stores/{store-id}/categories/all`**
- Retrieves all categories for a store.

#### 2. **`GET /stores/{store-id}/categories/category-id/{category-id}`**
- Retrieves a category by its ID within a store.

#### 3. **`GET /stores/{store-id}/categories/filters`**
- Retrieves categories filtered by name or type.
- Parameters:
    - `name` (optional): Filter by name.
    - `categoryType` (optional): Filter by category type.

#### 4. **`GET /stores/{store-id}/categories/product-id/{product-id}`**
- Retrieves categories associated with a specific product.

#### 5. **`POST /stores/{store-id}/categories`**
- Creates a new category.
- Request Body:
```
   {
        "name": "linen",
        "categoryType": "MATERIAL"
    }
```

#### 6. **`PUT /stores/{store-id}/categories/category-id/{category-id}`**
- Updates an existing category.
- Request Body
```
  {
  "name": "linen",
  "categoryType": "MATERIAL"
  }
```
#### 7. **`DELETE /stores/{store-id}/categories/category-id/{category-id}`**
- Deletes a category by its ID.

---

## Error Handling
### Custom Exceptions
- **`CategoryNotFoundException`**
    - Thrown when a category with the specified ID does not exist.

- **`CategoryAlreadyExist`**
    - Thrown when attempting to create or update a category with properties that already exist.

---

## CommandLineRunner: `insertCategory`
- **Order:** 3
- Can be removed/commented/deleted if wanted.
- Inserts predefined global categories (colors, materials, product types, and sizes) for a test store retrieved by email (`johndoe@gmail.com`).

---

## Design Highlights
1. **Dynamic Filtering**
    - Implemented using `Specification` to provide flexible query capabilities.

2. **Validation**
    - Used annotations like `@NotNull` and `@Size` for ensuring data integrity.

3. **Predefined Categories**
    - Categories such as colors, sizes, and materials are automatically populated for new stores.

4. **Relationship Management**
    - Categories are linked to products and stores, ensuring seamless integration with other modules.

5. **Optimized Insertions**
    - Efficient batch inserts using `saveAll` for predefined categories.

---

## Future Enhancements
1. Add pagination and sorting for category listings.
2. Introduce caching for frequently accessed categories.
3. Implement soft deletion for categories to preserve historical data.
4. Add multi-language support for category names and types.

