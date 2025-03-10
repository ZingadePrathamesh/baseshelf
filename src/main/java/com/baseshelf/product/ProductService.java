package com.baseshelf.product;

import com.baseshelf.printing.BarcodeService;
import com.baseshelf.brand.Brand;
import com.baseshelf.brand.BrandService;
import com.baseshelf.category.Category;
import com.baseshelf.category.CategoryService;
import com.baseshelf.order.ProductQuantityMap;
import com.baseshelf.store.Store;
import com.baseshelf.store.StoreNotFoundException;
import com.baseshelf.store.StoreService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final StoreService storeService;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final BarcodeService barcodeService;

    @Bean
    @Order(value = 6)
    public CommandLineRunner insertProducts(
            ProductRepository productRepository,
            StoreService storeService,
            BrandService brandService,
            CategoryService categoryService
    ){
        return args -> {
            Faker faker = new Faker();
            Store store = storeService.getByEmail("johndoe@gmail.com");
            List<Brand> brands = brandService.getAllBrandsByStore(store.getId());
            List<Category> materials = categoryService.getAllByIdOrNameOrCategoryType(store.getId(), null, null, "MATERIAL");
            List<Category> colors = categoryService.getAllByIdOrNameOrCategoryType(store.getId(), null, null, "COLOR");
            List<Category> productType = categoryService.getAllByIdOrNameOrCategoryType(store.getId(), null, null, "PRODUCT TYPE");
            List<Category> sizes = categoryService.getAllByIdOrNameOrCategoryType(store.getId(), null, null, "SIZE");
            Set<Product> products = new HashSet<>();

            for(int i =  0; i< 200; i++){
                Category material = materials.get(faker.number().numberBetween(0, materials.size()));
                Category color = colors.get(faker.number().numberBetween(0, colors.size()));
                Category productT = productType.get(faker.number().numberBetween(0, productType.size()));
                Category size = sizes.get(faker.number().numberBetween(0, sizes.size()));
                List<Category> categories = List.of(material, color, productT, size);
                float costPrize = faker.number().numberBetween(150, 10000);
                float sellingPrize = costPrize + costPrize * 40 / 100 ;
                float discountRate = faker.number().numberBetween(0, 20);

                Product product = Product.builder()
                        .name(color.getName()+ " " + productT.getName())
                        .description("Product with the size " + size.getName())
                        .categories(categories)
                        .cgst(18.0F)
                        .sgst(18.0F)
                        .costPrice(costPrize)
                        .sellingPrice(sellingPrize)
                        .discountRate(discountRate)
                        .taxed(true)
                        .quantity(faker.number().numberBetween(10, 100))
                        .isActive(true)
                        .brand(brands.get(faker.number().numberBetween(0, brands.size()-1)))
                        .store(store)
                        .build();
                products.add(product);
            }
            productRepository.saveAll(products);
            System.out.println("All products have been saved.");
        };
    }

    public Product getByIdAndStore(Long productId, Store store) {
        Optional<Product> productOptional = productRepository.findByIdAndStore(productId, store);
        return productOptional.orElseThrow(()->
                new ProductNotFoundException("Product with id: " + productId + " does not exist."));
    }

    public Product getByIdAndStore(Long productId, Long storeId) {
        Store store =  storeService.getById(storeId);
        Optional<Product> productOptional = productRepository.findByIdAndStore(productId, store);
        return productOptional.orElseThrow(()->
                new ProductNotFoundException("Product with id: " + productId + " does not exist."));
    }

    public List<Product> getAllProductsFromStore(Long storeId){
        Store store =  storeService.getById(storeId);
        return productRepository.findAllByStore(store);
    }

    public List<Product> getAllProductsFromStoreAndBrand(Long storeId, Long brandId){
        Store store = storeService.getById(storeId);
        Brand brand = brandService.getBrandById(storeId, brandId);
        return productRepository.findAllByStoreAndBrand(store, brand);
    }

    public List<Product> getAllProductsByCategories(Long storeId, List<Long> categoryIds){
        Store store = storeService.getById(storeId);

        long categoryCount = categoryIds.size();
        return productRepository.findByAllCategoryIdsAndStore(store, categoryIds, categoryCount);
    }

    public List<Product> getAllProductsWithoutCategories(Long storeId, String categoryType) {
        Store store = storeService.getById(storeId);
        return productRepository.findAllWithoutCategoryType(store, categoryType);
    }

    public List<Product> getAllProductsByStoreAndFilter(Long storeId, ProductFilter productFilter) {
        return productRepository.findAll(dynamicProductFilter(storeId, productFilter));
    }

    public List<Product> getAllByStoreAndIds(Long storeId, Set<Long> productIds){
        Store store = storeService.getById(storeId);
        return productRepository.findAllByStoreAndIdIn(store, productIds);
    }

    public Product saveProduct(Long storeId, @Valid Product product) {
        Store store = storeService.getById(storeId);
        Brand brand = brandService.getBrandById(storeId, product.getBrand().getId());
        List<Long> categoryIds = product.getCategories()
                .stream()
                .map(Category::getId)
                .toList();
        List<Category> categories = categoryService.getAllByStoreAndIdIn(storeId, categoryIds);

        product.setStore(store);
        product.setBrand(brand);
        product.setCategories(new ArrayList<>(categories)); // Convert Set to List

        // Save the product and generate the barcode
        Product savedProduct = productRepository.save(product);

        // Save the updated product with the generated barcode
        return productRepository.save(savedProduct);
    }

    public ResponseEntity<byte[]> getBarcodeForProduct(Long storeId, Long productId) throws OutputException, BarcodeException, IOException {
        Product product = this.getByIdAndStore(productId, storeId);
        BufferedImage bufferedImage = barcodeService.generateCode128BarcodeImage(product.getId().toString());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_PNG);
        httpHeaders.setContentLength(imageBytes.length);

        return new ResponseEntity<byte[]>(imageBytes, httpHeaders, HttpStatus.OK);
    }

    public ResponseEntity<byte[]> getProductLabel(Long storeId, Long productId) throws OutputException, BarcodeException, IOException {
        Product product = getByIdAndStore(productId, storeId);
        BufferedImage image = barcodeService.createProductLabel(product);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_PNG);
        httpHeaders.setContentLength(imageBytes.length);

        return new ResponseEntity<>(imageBytes, httpHeaders, HttpStatus.OK);
    }

    public ResponseEntity<StreamingResponseBody> getProductLabelStream(Long storeId, Long productId) throws OutputException, BarcodeException, IOException {
        Product product = getByIdAndStore(productId, storeId);
        BufferedImage image = barcodeService.createProductLabel(product);

        StreamingResponseBody streamingResponseBody = outputStream -> {
            ImageIO.write(image, "png", outputStream);
            outputStream.flush();
        };

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<>(streamingResponseBody, httpHeaders, HttpStatus.OK);
    }

    public ResponseEntity<StreamingResponseBody> getBarcodeByProductId(Long storeId, Long productId) throws OutputException, BarcodeException {
        Product product = getByIdAndStore(productId, storeId);
        BufferedImage bufferedImage = barcodeService.createBarcode(product.getId().toString());

        StreamingResponseBody streamingResponseBody = outputStream -> {
          ImageIO.write(bufferedImage, "png", outputStream);
          outputStream.flush();
        };

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<>(streamingResponseBody, httpHeaders, HttpStatus.OK);
    }

    @Transactional
    public Product updateProductByStoreAndId(Long storeId, Long productId, @Valid Product product) {
        Brand brand = brandService.getBrandById(storeId, product.getBrand().getId());
        List<Long> categoryIds = product.getCategories().stream()
                .map(Category::getId)
                .toList();
        List<Category> categories = categoryService.getAllByStoreAndIdIn(storeId, categoryIds);

        Product oldProduct = this.getByIdAndStore(productId, storeId);
        oldProduct.setName(product.getName());
        oldProduct.setDescription(product.getDescription());
        oldProduct.setSellingPrice(product.getSellingPrice());
        oldProduct.setCostPrice(product.getCostPrice());
        oldProduct.setTaxed(product.isTaxed());
        oldProduct.setCgst(product.getCgst());
        oldProduct.setSgst(product.getSgst());
        oldProduct.setQuantity(product.getQuantity());
        oldProduct.setCategories(categories.stream().toList());
        oldProduct.setBrand(brand);
        oldProduct.setActive(product.isActive());
        oldProduct.setLastModifiedOn(LocalDate.now());
        return oldProduct;
    }

    public void deleteById(Long storeId, List<Long> productIds){
        Store store =  storeService.getById(storeId);
        productRepository.deleteByStoreAndIdIn(store, productIds);
    }

    @Transactional
    public Product adjustStock(Long storeId, Long productId, Integer quantity){
        Product product = getByIdAndStore(productId, storeId);
        product.setQuantity(quantity);
        return product;
    }

    Specification<Product> dynamicProductFilter(Long storeId, ProductFilter productFilter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Ensure storeId is valid and fetch store
            if (storeId == null) {
                throw new StoreNotFoundException("Store ID is required");
            }
            Store store = storeService.getById(storeId);
            predicates.add(criteriaBuilder.equal(root.get("store"), store));

            // Filter by name with partial matching
            if (productFilter.getName() != null) {
                String pattern = "%" + productFilter.getName().trim() + "%";
                predicates.add(criteriaBuilder.like(root.get("name"), pattern));
            }

            // Filter by quantity range
            if (productFilter.getLowerQuantity() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("quantity"), productFilter.getLowerQuantity()));
            }
            if (productFilter.getHigherQuantity() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("quantity"), productFilter.getHigherQuantity()));
            }

            // Filter by price range
            if (productFilter.getLowerPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("sellingPrice"), productFilter.getLowerPrice()));
            }
            if (productFilter.getHigherPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("sellingPrice"), productFilter.getHigherPrice()));
            }

            // Filter by CGST/SGST
            if (productFilter.getCgst() != null) {
                predicates.add(criteriaBuilder.equal(root.get("cgst"), productFilter.getCgst()));
            }
            if (productFilter.getSgst() != null) {
                predicates.add(criteriaBuilder.equal(root.get("sgst"), productFilter.getSgst()));
            }

            // Filter by discount rate
            if (productFilter.getDiscountRateMoreThan() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("discountRate"), productFilter.getDiscountRateMoreThan()));
            }
            if (productFilter.getDiscountRateLessThan() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("discountRate"), productFilter.getDiscountRateLessThan()));
            }

            // Filter by HSN code
            if (productFilter.getHsnCode() != null) {
                predicates.add(criteriaBuilder.equal(root.get("hsnCode"), productFilter.getHsnCode().trim()));
            }

            // Filter by taxed flag
            if (productFilter.getTaxed() != null) {
                predicates.add(criteriaBuilder.equal(root.get("taxed"), productFilter.getTaxed()));
            }

            // Filter by unit of measure
            if (productFilter.getUnitOfMeasure() != null) {
                predicates.add(criteriaBuilder.equal(root.get("unitOfMeasure"), productFilter.getUnitOfMeasure().trim()));
            }

            // Filter by supplier IDs
            if (productFilter.getSupplierIds() != null && !productFilter.getSupplierIds().isEmpty()) {
                predicates.add(root.get("supplier").get("id").in(productFilter.getSupplierIds()));
            }

            // Filter by brand IDs
            if (productFilter.getBrandIds() != null && !productFilter.getBrandIds().isEmpty()) {
                predicates.add(root.get("brand").get("id").in(productFilter.getBrandIds()));
            }

            // Filter by last modified date range
            if (productFilter.getFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("lastModifiedOn"), productFilter.getFrom()));
            }
            if (productFilter.getTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("lastModifiedOn"), productFilter.getTo()));
            }

            // Filter by categories (ensure all specified categories match)
            if (productFilter.getCategoryIds() != null && !productFilter.getCategoryIds().isEmpty()) {
                query.distinct(true);
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<Product> subqueryRoot = subquery.from(Product.class);
                Join<Product, Category> categoryJoin = subqueryRoot.join("categories");

                subquery.select(subqueryRoot.get("id"))
                        .where(criteriaBuilder.and(
                                criteriaBuilder.equal(subqueryRoot.get("store"), store),
                                subqueryRoot.get("id").in(root.get("id")),
                                categoryJoin.get("id").in(productFilter.getCategoryIds())
                        ))
                        .groupBy(subqueryRoot.get("id"))
                        .having(criteriaBuilder.equal(criteriaBuilder.countDistinct(categoryJoin.get("id")), (long) productFilter.getCategoryIds().size()));

                predicates.add(root.get("id").in(subquery));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


    @Transactional
    public Map<Long, Product> validateProductsAndQuantity(Store store, List<ProductQuantityMap> productQuantity) {
        Set<Long> productIds = productQuantity
                .stream()
                .map(ProductQuantityMap::getProductId)
                .collect(Collectors.toSet());

        List<Product> products = productRepository.findAllByStoreAndIdIn(store, productIds);

        Map<Long, Product> productMap = products
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        for(ProductQuantityMap pm : productQuantity){
            Product product = productMap.get(pm.getProductId());
            if(product == null){
                throw new ProductNotFoundException("product with id: " + pm.getProductId() + " does not exist");
            }

            int newValue = product.getQuantity() - pm.getQuantity();
            if(newValue < 0){
                throw new ProductQuantityExceedException("Product with id: " + pm.getProductId() + " exceeds in quantity by "+ Math.abs(newValue));
            }
            else{
                product.setQuantity(newValue);
            }
        }
        return productMap;
    }

    @Transactional
    public Map<Long, Product> returnProducts(Store store, List<ProductQuantityMap> productQuantity) {
        Set<Long> productIds = productQuantity
                .stream()
                .map(ProductQuantityMap::getProductId)
                .collect(Collectors.toSet());

        List<Product> products = productRepository.findAllByStoreAndIdIn(store, productIds);

        Map<Long, Product> productMap = products
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        for(ProductQuantityMap pm: productQuantity){
            Product product = productMap.get(pm.getProductId());
            if(product == null){
                throw new ProductNotFoundException("product with id: " + pm.getProductId() + " does not exist");
            }
            product.setQuantity(product.getQuantity() + pm.getQuantity());
        }
        return productMap;
    }


}
