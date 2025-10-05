package ru.yandex.practicum.filmorate.storage;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<Long>> friendships = new HashMap<>();

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

    @Override
    public void addFriend(Long userId, Long friendId) {
        // Проверяем существование пользователей
        getUserById(userId);
        getUserById(friendId);

        if (userId.equals(friendId)) {
            throw new ValidationException("Пользователь не может добавить самого себя в друзья");
        }

        Set<Long> userFriends = friendships.get(userId);
        if (userFriends.contains(friendId)) {
            throw new ValidationException("Пользователь уже добавлен в друзья");
        }

        userFriends.add(friendId);
        log.info("Пользователь {} добавил в друзья пользователя {}", userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);

        Set<Long> userFriends = friendships.get(userId);
        if (!userFriends.contains(friendId)) {
            throw new NotFoundException("Пользователь с id=" + friendId
                    + " не найден в друзьях у пользователя с id=" + userId);
        }

        userFriends.remove(friendId);
        log.info("Пользователь {} удалил из друзей пользователя {}", userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        getUserById(userId);

        Set<Long> friendIds = friendships.get(userId);
        return friendIds.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        getUserById(userId);
        getUserById(otherUserId);

        Set<Long> userFriends = friendships.get(userId);
        Set<Long> otherUserFriends = friendships.get(otherUserId);

        Set<Long> commonFriendIds = new HashSet<>(userFriends);
        commonFriendIds.retainAll(otherUserFriends);

        return commonFriendIds.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
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
