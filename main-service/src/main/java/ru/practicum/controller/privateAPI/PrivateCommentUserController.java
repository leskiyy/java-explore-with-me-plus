package ru.practicum.controller.privateAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentWithEventDto;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/comments")
public class PrivateCommentUserController {

    @GetMapping
    public List<CommentWithEventDto> getUsersComment(@PathVariable Long userId,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        //TODO: Реализовать эндпоинт
        return null;
    }

    @DeleteMapping
    public void deleteAllUsersComments(@PathVariable Long userId) {
        //TODO: Реализовать эндпоинт
    }

}
