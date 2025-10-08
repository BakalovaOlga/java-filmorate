package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> getAllUsers();

    User createUser(User newUser);

    User updateUser(User user);

    void deleteUser(Long userId);

    Optional<User> getUserById(Long id);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<User> getFriends(Long userId);

    List<User> getCommonFriends(Long userId, Long otherUserId);

    boolean friendshipExists(Long userId, Long friendId);
}
