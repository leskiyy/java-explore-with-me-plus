package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.entity.Event;
import ru.practicum.entity.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    @Query("SELECT e FROM Event e WHERE e.state = :state " +
           "AND (:text IS NULL OR lower(e.annotation) LIKE lower(concat('%', :text, '%')) " +
           "OR lower(e.description) LIKE lower(concat('%', :text, '%'))) " +
           "AND (:categories IS NULL OR e.category.id IN :categories) " +
           "AND (:paid IS NULL OR e.paid = :paid) " +
           "AND (:start IS NULL OR e.eventDate >= :start) " +
           "AND (:end IS NULL OR e.eventDate <= :end)"
    )
    List<Event> findPublicEventsWithFilters(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("state") EventState state
    );

    Page<Event> findByInitiatorId(Long id, Pageable pageable);

    Optional<Event> findByIdAndState(Long id, EventState state);
}
