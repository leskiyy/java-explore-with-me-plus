package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.HitDto;
import ru.practicum.entity.Hit;
import ru.practicum.mapper.HitMapper;
import ru.practicum.repository.HitRepository;

@Service
@RequiredArgsConstructor
public class HitService {

    private final HitRepository hitRepository;
    private final HitMapper hitMapper;

    public void saveHit(HitDto hitDto) {
        Hit hit = hitMapper.toEntity(hitDto);
        hitRepository.save(hit);
    }
}
