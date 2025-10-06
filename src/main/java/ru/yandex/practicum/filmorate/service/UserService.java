package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
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
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }

    public void deleteUser(Long userId) {
        getUserById(userId); // проверяем что пользователь существует
        userStorage.deleteUser(userId);
    }

    public void addToFriends(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить себя в друзья");
        }

        if (userStorage.friendshipExists(userId, friendId)) {
            throw new ValidationException("Пользователь уже добавлен в друзья");
        }

        userStorage.addFriend(userId, friendId);
        log.info("Пользователь {} добавил в друзья пользователя {}", userId, friendId);
    }


    public void removeFromFriends(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (!userStorage.friendshipExists(userId, friendId)) {
            log.info("Пользователь с id={} не найден в друзьях у пользователя с id={}", friendId, userId);
        } else {
            userStorage.removeFriend(userId, friendId);
            log.info("Пользователь {} удалил из друзей пользователя {}", userId, friendId);
        }
    }

    public List<User> getUserFriends(Long id) {
        getUserById(id);
        return userStorage.getFriends(id);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        getUserById(userId);
        getUserById(otherUserId);
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
