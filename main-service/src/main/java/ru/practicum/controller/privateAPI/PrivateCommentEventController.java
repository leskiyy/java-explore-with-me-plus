package ru.practicum.controller.privateAPI;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CreateUpdateCommentDto;
import ru.practicum.dto.comment.PreModerationRequest;
import ru.practicum.service.CommentService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/events/{eventId}/comments")
public class PrivateCommentEventController {

    private final CommentService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CommentDto postComment(@PathVariable @Positive Long userId,
                                  @PathVariable @Positive Long eventId,
                                  @RequestBody @Valid CreateUpdateCommentDto dto) {
        log.info("Adding new comment userId={}, eventId={}, dto{}", userId, eventId, dto);
        return service.addComment(userId, eventId, dto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto editComment(@PathVariable @Positive Long userId,
                                  @PathVariable @Positive Long eventId,
                                  @PathVariable @Positive Long commentId,
                                  @RequestBody @Valid CreateUpdateCommentDto dto) {
        log.info("Updating new comment userId={}, eventId={}, dto{}", userId, eventId, dto);
        return service.updateComment(userId, eventId, commentId, dto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long eventId,
                              @PathVariable Long commentId) {
        service.deleteComment(userId, commentId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/pre-moderation")
    public void addPreModeration(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @RequestBody PreModerationRequest preModerationDto) {
        service.addPreModeration(userId, eventId, preModerationDto);
    }


}
