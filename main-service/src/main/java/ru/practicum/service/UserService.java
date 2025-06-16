package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.entity.User;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.repository.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Transactional(readOnly = false)
    public UserDto create(NewUserRequest user) {
        User saved = userRepository.save(mapper.toEntity(user));
        return mapper.toDto(saved);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void delete(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        userRepository.deleteById(userId);
    }

    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        List<User> users;
        if (ids != null) {
            users = userRepository.getUsersByIdIn(ids);
        } else {
            users = userRepository.findAll();
        }
        return users.stream()
                .skip(from)
                .limit(size)
                .map(mapper::toDto)
                .toList();
    }
}
