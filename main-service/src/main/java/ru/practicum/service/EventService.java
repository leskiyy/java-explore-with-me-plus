package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.StatsDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.entity.Category;
import ru.practicum.entity.Event;
import ru.practicum.entity.EventState;
import ru.practicum.entity.RequestStatus;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.parameters.EventUserSearchParam;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final ParticipationRequestRepository requestRepository;
    private final StatsClient statsClient;
    private final EventMapper eventMapper;

    public List<EventShortDto> getUsersEvents(EventUserSearchParam params) {
        Page<Event> events = eventRepository.findByInitiatorId(params.getUserId(), params.getPageable());

        List<Long> eventIds = events.stream().map(Event::getId).toList();
        Map<Long, Long> views = getViews(eventIds);
        Map<Long, Long> confirmedRequests = requestRepository.countRequestsByEventIdsAndStatus(eventIds,
                RequestStatus.CONFIRMED);

        return events.stream()
                .map(event -> {
                    EventShortDto shortDto = eventMapper.toShortDto(event);
                    shortDto.setViews(views.get(event.getId()));
                    shortDto.setConfirmedRequests(confirmedRequests.get(event.getId()));
                    return shortDto;
                })
                .toList();

    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public EventFullDto saveEvent(NewEventDto dto, Long userId) {
        Event saved = eventRepository.saveAndFlush(eventMapper.toEntity(dto, userId));
        EventFullDto fullDto = eventMapper.toFullDto(saved);
        fullDto.setViews(0L);
        fullDto.setConfirmedRequests(0L);
        return fullDto;
    }

    public List<EventShortDto> searchEvents(String text, List<Long> categories, Boolean paid, LocalDateTime start,
                                            LocalDateTime end, String sort, int from, int size) {

        List<Event> events = eventRepository.findPublicEventsWithFilters(text, categories, paid, start, end, EventState.PUBLISHED);

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();
        Map<Long, Long> views = getViews(eventIds);
        Map<Long, Long> confirmed = requestRepository.countRequestsByEventIdsAndStatus(eventIds,
                RequestStatus.CONFIRMED);
        return events.stream()
                .map(event -> {
                    EventShortDto dto = eventMapper.toShortDto(event);
                    dto.setConfirmedRequests(confirmed.get(dto.getId()));
                    dto.setViews(views.get(event.getId()));
                    return dto;
                })
                .collect(toList());
    }

    public EventFullDto getEventById(Long id) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Событие не найдено или не опубликовано"));
        Map<Long, Long> confirmed = requestRepository.countRequestsByEventIdsAndStatus(List.of(event.getId()), RequestStatus.CONFIRMED);
        Map<Long, Long> views = getViews(List.of(event.getId()));

        EventFullDto dto = eventMapper.toFullDto(event);
        dto.setConfirmedRequests(confirmed.get(dto.getId()));
        dto.setViews(views.get(dto.getId()));
        return dto;
    }

    public EventFullDto getEventByIdAndUserId(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("Событие добавленно не теущем пользователем");
        }
        Map<Long, Long> confirmed = requestRepository.countRequestsByEventIdsAndStatus(List.of(event.getId()), RequestStatus.CONFIRMED);
        Map<Long, Long> views = getViews(List.of(event.getId()));

        EventFullDto dto = eventMapper.toFullDto(event);
        dto.setConfirmedRequests(confirmed.get(dto.getId()));
        dto.setViews(views.get(dto.getId()));
        return dto;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public EventFullDto updateEventByUser(Long eventId, Long userId, UpdateEventUserRequest event) {
        Event eventToUpdate = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено id=" + eventId));
        if (!Objects.equals(eventToUpdate.getInitiator().getId(), userId) ||
            eventToUpdate.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Событие добавленно не теущем пользователем или уже было опубликовано");
        }
        updateNouNullFields(eventToUpdate, event);
        Event updated = eventRepository.save(eventToUpdate);

        Map<Long, Long> confirmed = requestRepository.countRequestsByEventIdsAndStatus(List.of(eventId), RequestStatus.CONFIRMED);
        Map<Long, Long> views = getViews(List.of(eventId));

        EventFullDto result = eventMapper.toFullDto(updated);
        result.setConfirmedRequests(confirmed.get(eventId));
        result.setViews(views.get(eventId));
        return result;
    }

    private void updateNouNullFields(Event eventToUpdate, UpdateEventUserRequest event) {
        if (event.getAnnotation() != null) eventToUpdate.setAnnotation(event.getAnnotation());
        if (event.getCategory() != null) eventToUpdate.setCategory(Category.builder().id(event.getCategory()).build());
        if (event.getDescription() != null) eventToUpdate.setDescription(event.getDescription());
        if (event.getEventDate() != null) eventToUpdate.setEventDate(event.getEventDate());
        if (event.getLocation() != null) {
            eventToUpdate.setLat(event.getLocation().getLat());
            eventToUpdate.setLon(event.getLocation().getLon());
        }
        if (event.getPaid() != null) eventToUpdate.setPaid(event.getPaid());
        if (event.getParticipantLimit() != null) eventToUpdate.setParticipantLimit(event.getParticipantLimit());
        if (event.getRequestModeration() != null) eventToUpdate.setRequestModeration(event.getRequestModeration());
        if (event.getTitle() != null) eventToUpdate.setTitle(event.getTitle());
    }

    /**
     * Getting stats from stats client
     */
    private Map<Long, Long> getViews(List<Long> eventIds) {
        List<StatsDto> stats = statsClient.getStats(
                "2000-01-01 00:00:00",
                "2100-01-01 00:00:00",
                eventIds.stream().map(id -> "/events/" + id).toList(),
                false);
        return stats.stream()
                .collect(toMap(statDto ->
                        Long.parseLong(statDto.getUri().replace("/events/", "")), StatsDto::getHits));
    }
}
