package ru.practicum.controller.publicAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.service.CompilationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations() {
        log.info("GET /compilations called");
        List<CompilationDto> compilations = compilationService.getAllCompilations();
        log.info("Returned {} compilations", compilations.size());
        return compilations;
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        log.info("GET /compilations/{} called", compId);
        CompilationDto compilation = compilationService.getCompilationById(compId);
        log.info("Returned compilation: id={}, title={}", compilation.getId(), compilation.getTitle());
        return compilation;
    }
}
