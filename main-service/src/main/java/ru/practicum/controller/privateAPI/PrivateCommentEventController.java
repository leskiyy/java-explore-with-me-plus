package ru.practicum.controller.privateAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CreateUpdateCommentDto;
import ru.practicum.dto.comment.PreModerationRequest;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/events{eventId}/comments")
public class PrivateCommentEventController {

    @PostMapping
    public CommentDto postComment(@PathVariable Long userId,
                                  @PathVariable Long eventId,
                                  @RequestBody CreateUpdateCommentDto dto) {
        //TODO: Реализовать эндпоинт
        return null;
    }

    @PatchMapping("/{commentId}")
    public CommentDto editComment(@PathVariable Long userId,
                                  @PathVariable Long eventId,
                                  @PathVariable Long commentId,
                                  @RequestBody CreateUpdateCommentDto dto) {
        //TODO: Реализовать эндпоинт
        return null;
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long eventId,
                              @PathVariable Long commentId) {
        //TODO: Реализовать эндпоинт
    }

    @PostMapping("/pre-moderation")
    public void addPreModeration(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @RequestBody PreModerationRequest preModerationDto) {
        //TODO: Реализовать эндпоинт Может и лист стрингов вернуь или булеан можно, как будто от в этом немного смысла
    }


}
