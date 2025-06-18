package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.Location;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.entity.Category;
import ru.practicum.entity.Event;
import ru.practicum.entity.User;

@Component
public class EventMapper {

    public EventShortDto toShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(0L)
                .build();
    }

    public EventFullDto toFullDto(Event event) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(0L)
                .createdOn(event.getCreatedOn() != null ? event.getCreatedOn().toString() : null)
                .description(event.getDescription())
                .location(toLocation(event.getLat(), event.getLon()))
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .build();
    }

    private Location toLocation(Double lat, Double lon) {
        if (lat == null || lon == null) return null;
        return Location.builder()
                .lat(lat)
                .lon(lon)
                .build();
    }

    private CategoryDto toCategoryDto(Category category) {
        if (category == null) return null;
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    private UserShortDto toUserShortDto(User user) {
        if (user == null) return null;
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
