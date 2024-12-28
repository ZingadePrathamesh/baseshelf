package com.baseshelf.category;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("stores/{store-id}/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/all")
    public List<Category> getAllCategories(
            @PathVariable(name = "store-id") Long storeId
    ){
        return categoryService.getAllByStore(storeId);
    }

    @GetMapping("category-id/{category-id}")
    public Category getById(
            @PathVariable(name = "store-id") Long storeId,
            @PathVariable(name = "category-id") Long id

    ){
        return categoryService.getByCategoryId(storeId, id);
    }

    @GetMapping("filters")
    public List<Category> getCategoriesByFilter(
            @PathVariable(name = "store-id") Long storeId,
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "categoryType") String categoryType
    ){
        return categoryService.getAllCategoriesByNameOrCategoryType(storeId, name, categoryType);
    }

    @GetMapping("product-id/{product-id}")
    public List<Category> getCategoriesByProduct(
            @PathVariable(name = "store-id") Long storeId,
            @PathVariable(name = "product-id") Long productId
    ){
        return categoryService.getAllCategoriesByProduct(storeId, productId);
    }

    @PostMapping("")
    public Category postCategory(
            @PathVariable(name = "store-id") Long storeId,
            @RequestBody Category newCategory
    ){
        return categoryService.saveCategory(storeId, newCategory);
    }

    @PutMapping("category-id/{category-id}")
    public Category updateCategoryById(
            @PathVariable(name = "store-id") Long storeId,
            @PathVariable(name = "category-id") Long id,
            @RequestBody Category newCategory){
        return categoryService.updateCategory(storeId ,id, newCategory);
    }

    @DeleteMapping("category-id/{category-id}")
    public void deleteById(
            @PathVariable(name = "store-id") Long storeId,
            @PathVariable(name = "category-id") Long categoryId
    ){
        categoryService.deleteByIdOrNameOrCategoryType(storeId, categoryId, null, null);
    }

}
