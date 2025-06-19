package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.entity.Event;
import ru.practicum.entity.ParticipationRequest;
import ru.practicum.entity.RequestStatus;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.ParticipationRequestMapper;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationRequestService {

    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestMapper requestMapper;

    public List<ParticipationRequestDto> getRequestForEventByUserId(Long eventId, Long userId) {
        List<ParticipationRequest> requests = requestRepository.findAllByRequesterIdAndEventId(userId, eventId);
        return requests.stream()
                .map(requestMapper::toDto)
                .toList();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public EventRequestStatusUpdateResult updateRequests(Long eventId,
                                                         Long userId,
                                                         EventRequestStatusUpdateRequest updateRequest) {
        List<ParticipationRequest> requestList = requestRepository.findAllById(updateRequest.getRequestIds());
        Event event = eventRepository
                .findById(eventId).orElseThrow(() -> new NotFoundException("There is no event id=" + eventId));
        updateRequests(requestList, updateRequest.getStatus(), event);

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        requestList.forEach(request -> {
            switch (request.getStatus()) {
                case RequestStatus.PENDING -> {
                    result.getConfirmedRequests().add(requestMapper.toDto(request));
                    result.getRejectedRequests().add(requestMapper.toDto(request));
                }
                case RequestStatus.REJECTED -> result.getRejectedRequests().add(requestMapper.toDto(request));
                case RequestStatus.CONFIRMED -> result.getConfirmedRequests().add(requestMapper.toDto(request));
            }
        });
        return result;
    }

    //почти 100 процентов это работать не будет, нужны тесты, чтобы понять, что вообще от нас хоят))
    private void updateRequests(List<ParticipationRequest> requests, RequestStatus status, Event event) {
        boolean hasNotPendingRequests = requests.stream().map(ParticipationRequest::getStatus).anyMatch(el -> el != RequestStatus.PENDING);
        if (hasNotPendingRequests)
            throw new ConflictException("Can't change status when request status is not PENDING");

        Boolean requestModeration = event.getRequestModeration();
        Integer participantLimit = event.getParticipantLimit();

        if (!requestModeration && participantLimit == null) {
            requests.forEach(request -> request.setStatus(status));
            return;
        }

        Long confirmedRequests = requestRepository
                .countRequestsByEventIdsAndStatus(List.of(event.getId()), RequestStatus.CONFIRMED)
                .get(event.getId());
        for (ParticipationRequest request : requests) {
            if (confirmedRequests >= participantLimit) {
                request.setStatus(RequestStatus.REJECTED);
            } else {
                request.setStatus(status);
                confirmedRequests++;
            }
        }
    }
}