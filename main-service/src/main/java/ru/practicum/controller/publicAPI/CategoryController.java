package ru.practicum.controller.publicAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        log.info("GET /categories called");
        List<CategoryDto> categories = categoryService.getAllCategories(pageable);
        log.info("Returned {} categories", categories.size());
        return categories;
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        log.info("GET /categories/{} called", catId);
        CategoryDto category = categoryService.getCategoryById(catId);
        log.info("Returned category: id={}, name={}", category.getId(), category.getName());
        return category;
    }
}
