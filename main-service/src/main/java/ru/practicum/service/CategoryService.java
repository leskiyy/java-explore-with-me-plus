package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.entity.Category;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;

    public CategoryDto save(NewCategoryDto dto) {
        Category saved = categoryRepository.save(mapper.toEntity(dto));
        return mapper.toDto(saved);
    }

    public void deleteCategory(Integer catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Category with id=" + catId + " was not found");
        }
        categoryRepository.deleteById(catId);
        categoryRepository.flush();
    }


    public CategoryDto update(CategoryDto dto) {
        if (!categoryRepository.existsById(dto.getId())) {
            throw new NotFoundException("Category with id=" + dto.getId() + " was not found");
        }
        Category updated = categoryRepository.saveAndFlush(mapper.toEntity(dto));
        return mapper.toDto(updated);
    }
}
