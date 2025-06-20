package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.entity.*;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.ParticipationRequestMapper;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationRequestService {

    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private final ParticipationRequestMapper requestMapper;

    public List<ParticipationRequestDto> getRequestForEventByUserId(Long eventId, Long userId) {
        List<ParticipationRequest> requests = requestRepository.findAllByRequesterIdAndEventId(userId, eventId);
        return requests.stream()
                .map(requestMapper::toDto)
                .toList();
    }

    public List<ParticipationRequestDto> getRequestsByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return requestRepository.findAllByRequesterId(userId)
                .stream()
                .map(requestMapper::toDto)
                .toList();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator cannot request participation in their own event");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Event must be published to request participation");
        }

        if (event.getParticipantLimit() != 0 &&
            requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED) >= event.getParticipantLimit()) {
            throw new ConflictException("Event participant limit reached");
        }

        ParticipationRequest request = new ParticipationRequest();
        request.setRequester(user);
        request.setEvent(event);
        request.setStatus(event.getRequestModeration() ? RequestStatus.PENDING : RequestStatus.CONFIRMED);
        request.setCreated(LocalDateTime.now());

        return requestMapper.toDto(requestRepository.save(request));
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

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new ConflictException("User is not the requester");
        }

        request.setStatus(RequestStatus.REJECTED);
        return requestMapper.toDto(requestRepository.save(request));
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