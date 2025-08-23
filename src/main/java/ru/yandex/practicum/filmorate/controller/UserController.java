package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User newUser) {
        validateUser(newUser);
        newUser.setId(getNextId());
        users.put(newUser.getId(), newUser);
        log.info("Пользователь с ID {} успешно добавлен", newUser.getId());
        return newUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getId() == null) {
            log.info("Не указан id для обновляемого пользователя. ");
            throw new ValidationException("Id обновляемого пользователя не задан.");
        }

        if (!users.containsKey(user.getId())) {
            log.info("Пользователь с id = {} не найден", user.getId());
            throw new NotFoundException("Пользователь с ID " + user.getId() + " не найден.");
        } else {
            validateUser(user);
            users.put(user.getId(), user);
            log.info("Пользователь с ID {} успешно обновлен", user.getId());
        }
        return user;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateUser(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы.");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Пустое отображаемое имя. Использован логин.");
        }

    }
}
