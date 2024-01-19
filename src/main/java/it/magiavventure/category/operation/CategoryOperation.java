package it.magiavventure.category.operation;

import it.magiavventure.category.model.Category;
import it.magiavventure.category.model.CreateCategory;
import it.magiavventure.category.model.UpdateCategory;
import it.magiavventure.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/categories")
public class CategoryOperation {

    private final CategoryService categoryService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category createCategory(@RequestBody @Valid CreateCategory createCategory) {
        return categoryService.createCategory(createCategory);
    }

    @GetMapping
    public List<Category> retrieveCategories() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public Category retrieveCategory(@PathVariable(name = "id") UUID id) {
        return categoryService.findById(id);
    }

    @PutMapping
    public Category updateCategory(@RequestBody @Valid UpdateCategory updateCategory) {
        return categoryService.updateCategory(updateCategory);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(name = "id") UUID id) {
        categoryService.deleteById(id);
    }
}
