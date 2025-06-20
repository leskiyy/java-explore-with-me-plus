package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.entity.Event;
import ru.practicum.entity.EventState;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    @EntityGraph(attributePaths = {"initiator.id", "initiator.name", "category.id", "category.name"})
    Page<Event> findByInitiatorId(Long id, Pageable pageable);

    @EntityGraph(attributePaths = {"initiator.id", "initiator.name", "category.id", "category.name"})
    Optional<Event> findByIdAndState(Long id, EventState state);
}
