package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.repository.CompilationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    public List<CompilationDto> getAllCompilations() {
        return compilationRepository.findAll().stream()
                .map(compilationMapper::toDto)
                .toList();
    }

    public CompilationDto getCompilationById(Long compId) {
        return compilationRepository.findById(compId)
                .map(compilationMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Подборка не найдена"));
    }
}
