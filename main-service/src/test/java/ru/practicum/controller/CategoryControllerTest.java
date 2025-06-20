package ru.practicum.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.controller.publicAPI.CategoryController;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.CategoryService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

//    @Test
//    void shouldReturnAllCategories() throws Exception {
//        List<CategoryDto> categories = List.of(
//                CategoryDto.builder().id(1L).name("Concerts").build(),
//                CategoryDto.builder().id(2L).name("Theatre").build()
//        );
//        when(categoryService.getAllCategories()).thenReturn(categories);
//
//        mockMvc.perform(get("/categories"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(categories.size()))
//                .andExpect(jsonPath("$[0].id").value(categories.get(0).getId().intValue()))
//                .andExpect(jsonPath("$[0].name").value(categories.get(0).getName()))
//                .andExpect(jsonPath("$[1].id").value(categories.get(1).getId().intValue()))
//                .andExpect(jsonPath("$[1].name").value(categories.get(1).getName()));
//
//        verify(categoryService, times(1)).getAllCategories();
//        verifyNoMoreInteractions(categoryService);
//    }

    @Test
    void shouldReturnCategoryById() throws Exception {
        long catId = 42L;
        CategoryDto dto = CategoryDto.builder().id(catId).name("Cinema").build();

        when(categoryService.getCategoryById(catId)).thenReturn(dto);

        mockMvc.perform(get("/categories/{catId}", catId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value((int) catId))
                .andExpect(jsonPath("$.name").value("Cinema"));

        verify(categoryService, times(1)).getCategoryById(catId);
        verifyNoMoreInteractions(categoryService);
    }
}