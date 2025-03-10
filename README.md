# BaseShelf
(In Progress...)
BaseShelf is a REST API server designed to help users manage their products, analyze sales, and gain useful insights, all with high-level flexibility and performance. The project follows best practices for Spring Boot, emphasizing performance tuning and clean architecture, making it a versatile platform for integration with various frontends.

The server includes several powerful features and uses modern tools and technologies:

## **Key Technologies**
- **Spring Boot 3.4.0**
- **Spring Data JPA**
- **Spring Security**
- **Spring Validation**
- **Docker**
- **PostgreSQL**
- **REST Architecture**

---

## **About The Project**

### **Features**
1. **Store Management**
   - Users can register their store, which serves as their profile.
   - Use the platform to manage store data and operations.

2. **Product, Product Category & Brand Management**
   - Create and manage brands (e.g., Nike, Adidas, etc.).
   - Define product categories (e.g., material, color, patterns) with the option to use predefined global categories.
   - Add products by associating them with brands and categories.

3. **Order Generation**
   - Multiple options for order creation while selling products.
   - Orders are validated against product availability.
   - Restocking and return product registration.

4. **Barcode & Label Generation**
   - Generate barcodes for specific products.
   - Create predesigned printable labels with product details and barcode images.

5. **Receipt Invoice Generation**
   - Generate and manage customer invoices seamlessly.

6. **Analytics & Reports**
   - Detailed revenue, quantity, profit percentages, and other metrics.
   - Analyze orders, brands, and categories by month, year, or date range.
   - Export reports as Excel sheets.

7. **Bulk Uploading**
   - Upload products, categories, and brands using CSV files.

---

### **Project Aspects**
The application is modular and organized into the following key aspects:
1. **Base Entity**: Common fields and logic shared across entities. It is extended by almost all entities.
2. **Store Management**: [Read about store module](https://github.com/ZingadePrathamesh/baseshelf/blob/0e886f2cbe23e000898c6db10c29f2b5061a2517/src/main/java/com/baseshelf/store/store_readme.md)
3. **Brand Management**: [Read about brand module](https://github.com/ZingadePrathamesh/baseshelf/blob/65839cca89be70a260a0d1acce5f5ed52e30335e/src/main/java/com/baseshelf/brand/BRAND.md)
4. **Categories**: [Read about category module](https://github.com/ZingadePrathamesh/baseshelf/blob/e0263a8411caba1ce30c879e796a9af518fd9fba/src/main/java/com/baseshelf/category/CATEGORY.md)
5. **Product Management**: CRUD operations for products.
6. **Order & Order Items**: Handling of customer orders and associated items.
7. **Barcode & Label Generator**: Efficiently generate barcodes and labels.
8. **Analytics**: Comprehensive analysis and insights.
9. **Reports**: Exportable reports in Excel format.

---

## **Educational Resources**
This project incorporates learnings from various resources to ensure best practices:
- **Performance Tuning**: [6 Performance Pitfalls with Spring Data JPA](https://thorben-janssen.com/6-performance-pitfalls-when-using-spring-data-jpa/)
- **Spring Data JPA Tutorial**: [YouTube Video](https://youtu.be/mcl_nibV39s?si=F_1f4Ce9bovhBEtd)
- **Barcode Generator**: [Baeldung Article](https://www.baeldung.com/java-generating-barcodes-qr-codes)
- **SQL**: [W3SCHOOL SQL](https://www.w3schools.com/SQL/deFault.asp)
- **GST Invoice Compliance**: [GST compliance guide](https://cleartax.in/s/gst-invoice)

---

## **Getting Started**

### **Prerequisites**
- **Java 17+**
- **Maven 3.8+**
- **Docker** 

### **Installation**
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/baseshelf.git
2. Install Docker Desktop and run it.
3. Open command prompt and run the docker script:
   ```bash
   docker run -d  --name baseshelf  -e POSTGRES_PASSWORD=2024baseshelf@  -e POSTGRES_USER=baseshelf  -e POSTGRES_DB=baseshelfdb1  -e POSTGRES_HOST_AUTH_METHOD=trust  -p 5436:5432  postgres:latest
4. Run the application in your IDE
