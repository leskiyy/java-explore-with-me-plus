package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CreateUpdateCommentDto;
import ru.practicum.entity.Comment;
import ru.practicum.entity.Event;
import ru.practicum.entity.User;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper mapper;

    public CommentDto addComment(Long userId, Long eventId, CreateUpdateCommentDto dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Set<String> forbiddenWords = event.getForbiddenWords();
        if (forbiddenWords != null && !forbiddenWords.isEmpty()) {
            String contentLower = dto.getContent().toLowerCase();
            boolean hasForbidden = forbiddenWords.stream()
                    .anyMatch(word -> contentLower.contains(word.toLowerCase()));
            if (hasForbidden) {
                throw new ForbiddenException("Комментарий содержит запрещённые слова");
            }
        }

        Comment comment = new Comment();
        comment.setEvent(event);
        comment.setAuthor(author);
        comment.setContent(dto.getContent());
        comment.setCreated(LocalDateTime.now());
        comment.setUpdated(LocalDateTime.now());

        comment = commentRepository.save(comment);

        return mapper.toDto(comment);
    }

    public CommentDto updateComment(Long userId, Long commentId, CreateUpdateCommentDto dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        if (comment.getAuthor() == null || !comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("Редактировать можно только свои комментарии");
        }

        Set<String> forbiddenWords = comment.getEvent().getForbiddenWords();
        if (forbiddenWords != null && !forbiddenWords.isEmpty()) {
            String contentLower = dto.getContent().toLowerCase();
            boolean hasForbidden = forbiddenWords.stream()
                    .anyMatch(word -> contentLower.contains(word.toLowerCase()));
            if (hasForbidden) {
                throw new ForbiddenException("Комментарий содержит запрещённые слова");
            }
        }

        comment.setContent(dto.getContent());
        comment.setUpdated(LocalDateTime.now());

        comment = commentRepository.save(comment);

        return mapper.toDto(comment);
    }

    public List<CommentDto> getEventComments(Long eventId) {
        return commentRepository.findByEventId(eventId).stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<CommentDto> getAllUserComments(Long userId) {
        return commentRepository.findByAuthorId(userId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("Удалять можно только свои комментарии");
        }
        commentRepository.delete(comment);
    }

    public void deleteAllUserComments(Long userId) {
        List<Comment> comments = commentRepository.findByAuthorId(userId);
        commentRepository.deleteAll(comments);
    }
}