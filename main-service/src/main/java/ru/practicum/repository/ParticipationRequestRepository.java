package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.entity.ParticipationRequest;
import ru.practicum.entity.RequestStatus;

import java.util.List;
import java.util.Map;
import ru.practicum.entity.RequestStatus;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Integer> {
    @Query("""
            select r.event.id, count(r.id)
            from ParticipationRequest r
            where r.event.id in ?1 and r.status = ?2
            group by r.event.id""")
    Map<Long, Long> countRequestsByIdsAndStatus(List<Long> ids, RequestStatus status);

    long countByEventIdAndStatus(Long eventId, RequestStatus status);
}