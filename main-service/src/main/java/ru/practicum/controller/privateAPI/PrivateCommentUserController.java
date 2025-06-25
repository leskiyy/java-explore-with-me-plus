package ru.practicum.controller.privateAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentWithEventDto;
import ru.practicum.parameters.PageableSearchParam;
import ru.practicum.service.CommentService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/comments")
public class PrivateCommentUserController {

    private final CommentService service;

    @GetMapping
    public List<CommentWithEventDto> getUsersComment(@PathVariable Long userId,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        log.info("Getting user's comments userId={}", userId);
        PageableSearchParam param = PageableSearchParam.builder()
                .from(from)
                .size(size)
                .build();
        return service.getUsersComments(userId, param.getPageable());
    }

    @DeleteMapping
    public void deleteAllUsersComments(@PathVariable Long userId) {
        //TODO: Реализовать эндпоинт
    }

}
