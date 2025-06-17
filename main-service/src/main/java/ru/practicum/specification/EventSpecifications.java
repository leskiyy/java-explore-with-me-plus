package ru.practicum.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.entity.Event;

public class EventSpecifications {
    public static Specification<Event> userIdIs(Long userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("initiator_id"), userId);
    }
}
