package ru.practicum.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {
    @JsonFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    LocalDateTime created;
    Long event;
    Long id;
    Long requester;
    RequestStatus status;
}
