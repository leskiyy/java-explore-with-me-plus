package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.StatsDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.entity.Event;
import ru.practicum.entity.EventState;
import ru.practicum.entity.RequestStatus;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ru.practicum.controller.PrivateEventController;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.entity.Event;
import ru.practicum.mapper.EventMapper;
import ru.practicum.repository.EventRepository;
import ru.practicum.specification.EventSpecifications;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;


    private final StatsClient statsClient;
    private final ParticipationRequestRepository requestRepository;

    public List<EventShortDto> getUsersEvents(PrivateEventController.EventUserSearchParam params) {
        Page<Event> events = eventRepository.findByInitiator_Id(params.getUserId(), params.getPageable());
        events.stream().map()
    }

    public List<EventShortDto> searchEvents(String text, List<Long> categories, Boolean paid, LocalDateTime start,
                                            LocalDateTime end, String sort, int from, int size) {

        List<Event> events = eventRepository.findPublicEventsWithFilters(text, categories, paid, start, end, EventState.PUBLISHED);

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> views;
        if ("VIEWS".equalsIgnoreCase(sort)) {
            List<String> uris = eventIds.stream()
                    .map(id -> "/events/" + id)
                    .collect(Collectors.toList());

            List<StatsDto> stats = statsClient.getStats(
                    (start != null ? start.toString() : "2000-01-01 00:00:00"),
                    (end != null ? end.toString() : "2100-01-01 00:00:00"),
                    uris,
                    false
            );
            views = stats.stream().collect(Collectors.toMap(
                    s -> Long.valueOf(s.getUri().replace("/events/", "")),
                    StatsDto::getHits
            ));
        } else {
            views = Collections.emptyMap();
        }

        List<EventShortDto> result = events.stream()
                .map(event -> {
                    long confirmed = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
                    EventShortDto dto = eventMapper.toShortDto(event);
                    dto.setConfirmedRequests(confirmed);

                    Long viewsCount = views.get(event.getId());
                    dto.setViews(viewsCount == null ? 0L : viewsCount.longValue());
                    return dto;
                })
                .collect(Collectors.toList());

        return result;
    }

    public EventFullDto getEventById(Long id) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Событие не найдено или не опубликовано"));
        long confirmed = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);

        Long views = statsClient.getStats(
                "2000-01-01 00:00:00",
                "2100-01-01 00:00:00",
                List.of("/events/" + id),
                false
        ).stream()
                .findFirst()
                .map(StatsDto::getHits)
                .orElse(0L);

        EventFullDto dto = eventMapper.toFullDto(event);
        dto.setConfirmedRequests(confirmed);
        dto.setViews(views);
        return dto;
    }
}
