package ru.practicum.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.service.EventService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {

    private final EventService service;

    @GetMapping
    public List<EventShortDto> getUsersEvents(@PathVariable @Positive Long userId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Getting events by user id={}, from={}, size={}", userId, from, size);
        EventUserSearchParam build = EventUserSearchParam.builder()
                .userId(userId)
                .from(from)
                .size(size)
                .build();
        return service.getUsersEvents(params);
    }

    @AllArgsConstructor
    @Builder
    @Getter
    public static class EventUserSearchParam {
        private Long userId;
        private Integer from;
        private Integer size;

        public Pageable getPageable() {
            int page = from / size;
            return PageRequest.of(page, size);
        }
    }
}
