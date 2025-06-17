package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.entity.ParticipationRequest;
import ru.practicum.entity.RequestStatus;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    long countByEventIdAndStatus(Long eventId, RequestStatus status);
}