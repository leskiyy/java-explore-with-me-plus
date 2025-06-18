package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.entity.Category;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;

    public CategoryDto save(NewCategoryDto dto) {
        Category saved = categoryRepository.save(mapper.toEntity(dto));
        return mapper.toDto(saved);
    }

    public void deleteCategory(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        categoryRepository.delete(category);
        categoryRepository.flush();
    }

    public CategoryDto update(CategoryDto dto) {
        Category category = categoryRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Category with id=" + dto.getId() + " was not found"));
        category.setName(dto.getName());
        categoryRepository.saveAndFlush(category);
        return mapper.toDto(category);
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public CategoryDto getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new NotFoundException("Категория не найдена"));
    }
}
