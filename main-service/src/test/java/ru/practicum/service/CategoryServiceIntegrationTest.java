package ru.practicum.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.IntegrationTestBase;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.entity.Category;
import ru.practicum.exception.NotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CategoryServiceIntegrationTest extends IntegrationTestBase {

    private final CategoryService service;
    private final EntityManager entityManager;

    @Test
    void save_whenOk() {
        String name = "name";
        CategoryDto savedDto = service.save(NewCategoryDto.builder()
                .name(name)
                .build());

        TypedQuery<Category> query = entityManager.createQuery("select c from Category c where c.id = :id",
                Category.class);

        Category saved = query.setParameter("id", savedDto.getId()).getSingleResult();
        assertThat(saved)
                .hasFieldOrPropertyWithValue("name", name)
                .hasFieldOrPropertyWithValue("id", savedDto.getId());
    }

    @Test
    void save_whenNameIsBusy() {
        String name = "name1";

        assertThatThrownBy(() -> service.save(NewCategoryDto.builder()
                .name(name)
                .build())).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void deleteCategory_whenOk() {
        Integer catId = 3;
        service.deleteCategory(catId);

        Category category = entityManager.find(Category.class, catId);
        assertThat(category).isNull();
    }

    @Test
    void deleteCategory_whenNotFound() {
        Integer catId = 999;

        assertThatThrownBy(() -> service.deleteCategory(catId)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteCategory_whenEventHasCategory() {
        Integer catId = 1;

        assertThatThrownBy(() -> service.deleteCategory(catId)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void update_whenOk() {
        Integer catId = 1;
        String name = "updated";
        service.update(CategoryDto.builder()
                .name(name)
                .id(catId)
                .build());

        TypedQuery<Category> query = entityManager.createQuery("select c from Category c where c.id = :id",
                Category.class);

        Category saved = query.setParameter("id", catId).getSingleResult();
        assertThat(saved)
                .hasFieldOrPropertyWithValue("name", name)
                .hasFieldOrPropertyWithValue("id", catId);
    }

    @Test
    void update_whenNameIsBusy() {
        Integer catId = 1;
        String name = "name2";
        CategoryDto build = CategoryDto.builder()
                .name(name)
                .id(catId)
                .build();

        assertThatThrownBy(() -> service.update(build)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void update_whenNotFound() {
        Integer catId = 999;
        String name = "999";
        CategoryDto build = CategoryDto.builder()
                .name(name)
                .id(catId)
                .build();

        assertThatThrownBy(() -> service.update(build)).isInstanceOf(NotFoundException.class);
    }
}