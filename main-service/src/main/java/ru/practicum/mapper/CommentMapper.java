package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentWithEventDto;
import ru.practicum.dto.comment.CommentWithUserDto;
import ru.practicum.entity.Comment;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    CommentDto toDto(Comment comment);

    CommentWithUserDto toWithUserDto(Comment comment);

    CommentWithEventDto toWithEventDto(Comment comment);
}