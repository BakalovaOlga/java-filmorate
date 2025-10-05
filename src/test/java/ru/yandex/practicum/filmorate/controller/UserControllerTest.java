package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserStorage userStorage;
    private UserService userService;
    private User user;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        user = new User(1L, "hello@email.com", "neo", "Neo",
                LocalDate.of(1990, 2, 6));
    }

    @Test
    void shouldAdduser() {
        User added = userStorage.createUser(user);
        assertNotNull(added.getId());
        assertEquals("Neo", added.getName());
    }

    @Test
    void shouldSetNameToLoginIfNameIsNull() {
        user.setName(null);
        User added = userService.createUser(user);
        assertEquals("neo", added.getName());
    }

    @Test
    void shouldSetNameToLoginIfNameIsBlank() {
        user.setName(" ");
        User added = userService.createUser(user);
        assertEquals("neo", added.getName());
    }

    @Test
    void shouldUpdateExistingUser() {
        User added = userStorage.createUser(user);
        added.setName("Нео");
        User updated = userStorage.updateUser(added);

        assertEquals("Нео", updated.getName());
        assertEquals(added.getId(), updated.getId());
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingNonExistentUser() {
        user.setId(999L); // Несуществующий ID
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> userStorage.updateUser(user));
        assertEquals("Пользователь с ID 999 не найден.", ex.getMessage());
    }

    @Test
    void shouldReturnAllUsers() {
        userStorage.createUser(user);
        Collection<User> users = userStorage.getAllUsers();
        assertEquals(1, users.size());
    }
}