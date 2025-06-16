package ru.practicum.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {
    List<Integer> events;
    Boolean pinned;
    @Size(min = 1, max = 50)
    String title;
}
