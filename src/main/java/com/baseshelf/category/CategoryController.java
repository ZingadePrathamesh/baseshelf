package com.baseshelf.category;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("")
    public List<Category> getAllCategories(){
        return categoryService.getAllByIdOrNameOrCategoryType(null, null, null);
    }

    @GetMapping("id/{id}")
    public Category getById(@PathVariable(name = "id") Long id){
        return categoryService.getCategoryById(id);
    }

    @GetMapping("filters")
    public List<Category> getCategoriesByFilter(
            @RequestParam(required = false, name = "id") Long id,
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "categoryType") String categoryType
    ){
        return categoryService.getAllByIdOrNameOrCategoryType(id, name, categoryType);
    }

    @PutMapping("id/{id}")
    public Category updateCategoryById(@PathVariable(name = "id") Long id, @RequestBody Category newCategory){
        return categoryService.updateCategory(id, newCategory);
    }

    @DeleteMapping("id/{id}")
    public void deleteById(@PathVariable(name = "id") Long id){
        categoryService.deleteByIdOrNameOrCategoryType(id, null, null);
    }
}
