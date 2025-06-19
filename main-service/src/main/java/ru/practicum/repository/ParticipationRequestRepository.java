package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.entity.ParticipationRequest;
import ru.practicum.entity.RequestStatus;

import java.util.List;
import java.util.Map;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    @Query("""
            select r.event.id, count(r.id)
            from ParticipationRequest r
            where r.event.id in ?1 and r.status = ?2
            group by r.event.id""")
    Map<Long, Long> countRequestsByEventIdsAndStatus(List<Long> ids, RequestStatus status);

    List<ParticipationRequest> findAllByRequesterIdAndEventId(Long requesterId, Long eventId);

}