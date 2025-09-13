package ru.yandex.practicum.filmorate.storage;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с ID: " + id + " не найден");
        }
        return users.get(id);
    }

    @Override
    public User createUser(User newUser) {
        newUser.setId(getNextId());
        users.put(newUser.getId(), newUser);
        log.info("Пользователь с ID {} успешно добавлен", newUser.getId());
        return newUser;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.info("Пользователь с id = {} не найден", user.getId());
            throw new NotFoundException("Пользователь с ID " + user.getId() + " не найден.");
        } else {
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
}
