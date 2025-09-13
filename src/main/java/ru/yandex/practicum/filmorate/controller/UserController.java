package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User newUser) {
        return userService.createUser(newUser);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendUserId}")
    public void addToFriends(@PathVariable("id") Long id, @PathVariable("friendUserId") Long friendUserId) {
        log.info("Пользователь с ID: {} добавил в друзья пльзователя с ID: {}", id, friendUserId);
        userService.addToFriends(id, friendUserId);
    }

    @DeleteMapping("/{id}/friends/{friendUserId}")
    public void removeFromFriends(@PathVariable("id") Long id, @PathVariable("friendUserId") Long friendUserId) {
        log.info("Пользователь с ID: {} убрал из друзей пльзователя с ID: {}", id, friendUserId);
        userService.removeFromFriends(id, friendUserId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable("id") Long id) {
        log.info("Получен список друзей пользователя с ID: {}", id);
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherUserId}")
    public List<User> getMutualFriends(@PathVariable("id") Long id, @PathVariable("otherUserId") Long otherUserId) {
        log.info("Получен список общих друзей пользователя с ID: {} и {}", id, otherUserId);
        return userService.getCommonFriends(id, otherUserId);
    }
}
