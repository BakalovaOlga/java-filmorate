package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;


@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        validateUser(user);
        return userStorage.createUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            log.info("Не указан id для обновляемого пользователя.");
            throw new ValidationException("Id обновляемого пользователя не задан.");
        }
        userStorage.getUserById(user.getId());
        return userStorage.updateUser(user);
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public void addToFriends(Long userId, Long friendUserId) {
        userStorage.addFriend(userId, friendUserId);
        log.info("Пользователь {} добавил в друзья пользователя {}", userId, friendUserId);
    }

    public void removeFromFriends(Long userId, Long friendUserId) {
        userStorage.removeFriend(userId, friendUserId);
        log.info("Пользователь {} удалил из друзей пользователя {}", userId, friendUserId);
    }

    public List<User> getUserFriends(Long id) {
        return userStorage.getFriends(id);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        return userStorage.getCommonFriends(userId, otherUserId);
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
