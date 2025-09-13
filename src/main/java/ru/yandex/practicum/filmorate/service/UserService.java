package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public User createUser(User user) {
        validateUser(user);
        return userStorage.createUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            log.info("Не указан id для обновляемого пользователя. ");
            throw new ValidationException("Id обновляемого пользователя не задан.");
        }
        return userStorage.updateUser(user);
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public void addToFriends(Long userId, Long friendUSerId) {
        User user = userStorage.getUserById(userId);
        User friendUser = userStorage.getUserById(friendUSerId);

        user.getFriends().add(friendUser.getId());
        friendUser.getFriends().add(user.getId());
    }

    public void removeFromFriends(Long userId, Long friendUSerId) {
        User user = userStorage.getUserById(userId);
        User friendUser = userStorage.getUserById(friendUSerId);

        user.getFriends().remove(friendUser.getId());
        friendUser.getFriends().remove(user.getId());
    }

    public List<User> getUserFriends(Long id) {
        Set<Long> userFriendsIds = userStorage.getUserById(id).getFriends();

        return userFriendsIds.stream()
                .map(userStorage::getUserById)
                .toList();
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        Set<Long> commonFriends = userStorage.getUserById(userId).getFriends();
        commonFriends.retainAll(userStorage.getUserById(otherUserId).getFriends());

        return commonFriends.stream()
                .map(userStorage::getUserById)
                .toList();
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
