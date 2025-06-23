package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.comment.*;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;

import java.util.List;

public interface CommentService {
    /**
     * Returns CommentDto if creating comment successful
     *
     * @param userId
     * @param eventId
     * @param dto
     * @throws NotFoundException  if event or user not found
     * @throws ForbiddenException if event has pre moderation and
     *                            comment's content is not allowed
     **/
    CommentDto postComment(Long userId, Long eventId, CreateUpdateCommentDto dto);

    /**
     * Returns CommentDto if updating comment successful
     *
     * @param userId
     * @param eventId
     * @param dto
     * @throws NotFoundException  if event, user or comment not found
     * @throws ForbiddenException if event has pre moderation and
     *                            comment's content is not allowed
     **/
    CommentDto updateComment(Long userId, Long eventId, CreateUpdateCommentDto dto);

    /**
     * Delete comment by comment id
     *
     * @param userId
     * @param eventId
     * @param commentId
     * @throws NotFoundException if event, user or comment not found
     * @throws ConflictException if comment is written not by user id=userId
     **/
    void deleteComment(Long userId, Long eventId, Long commentId);

    /**
     * Delete comment by commentId by admin
     *
     * @param commentId
     * @throws NotFoundException if comment not found
     **/
    void deleteCommentByAdmin(Long commentId);

    /**
     * Delete all user's comments id userId
     *
     * @param userId
     * @throws NotFoundException if user is not found
     **/
    void deleteCommentsByUser(Long userId);

    /**
     * Add Set of Sting from PreModerationRequest.forbiddenWords to existing Event.forbiddenWords or create a new one,
     * after adding new stop word make moderation of existing comments
     *
     * @param userId
     * @param eventId
     * @param preModerationDto
     * @throws NotFoundException if event or user not found
     * @throws ConflictException if event's id=eventId initiator is not user id=userId
     **/
    void addPreModeration(Long userId, Long eventId, PreModerationRequest preModerationDto);

    /**
     * Returns List of CommentWithEventDto by userId
     *
     * @param userId
     * @param pageable
     * @throws NotFoundException if user not found
     **/
    List<CommentWithEventDto> getUsersComments(Long userId, Pageable pageable);

    /**
     * Returns List of CommentWithEventDto by userId
     *
     * @param eventId
     * @param pageable
     * @throws NotFoundException if event not found
     **/
    List<CommentWithUserDto> getCommentsByEventId(Long eventId, Pageable pageable);
}
