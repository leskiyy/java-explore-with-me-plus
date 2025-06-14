package ru.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Slf4j
@WebMvcTest(AdminUserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class AdminUserControllerTest {

    private final ObjectMapper objectMapper;
    private final MockMvc mvc;

    @MockBean
    private final UserService service;

    @Test
    @SneakyThrows
    void getUsers() {
    }

    @Test
    @SneakyThrows
    void create() {
        String errorResponse = mvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(NewUserRequest.builder()
                                .email("")
                                .name("name")
                                .build())))
                .andReturn()
                .getResponse()
                .getContentAsString();
        log.info("ANSWER : {}", errorResponse);
//        assertThat(errorResponse).contains("errors").contains("reason").contains("status");
    }

    @Test
    void delete() {
    }
}