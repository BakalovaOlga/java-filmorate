package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
class UserControllerTest {

    @Autowired
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        userService.getAllUsers().forEach(u -> userService.deleteUser(u.getId()));

        user = new User(null, "hello@email.com", "neo", "Neo",
                LocalDate.of(1990, 2, 6));
    }

    @Test
    void shouldAddUser() {
        User added = userService.createUser(user);
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
        User added = userService.createUser(user);
        added.setName("Нео");
        User updated = userService.updateUser(added);

        assertEquals("Нео", updated.getName());
        assertEquals(added.getId(), updated.getId());
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingNonExistentUser() {
        user.setId(999L); // Несуществующий ID
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> userService.updateUser(user));
        assertEquals("Пользователь с ID 999 не найден.", ex.getMessage());
    }

    @Test
    void shouldReturnAllUsers() {
        userService.createUser(user);
        List<User> users = userService.getAllUsers();
        assertEquals(1, users.size());
    }

    @Test
    void shouldGetUserById() {
        User added = userService.createUser(user);
        User found = userService.getUserById(added.getId());

        assertEquals(added.getId(), found.getId());
        assertEquals("Neo", found.getName());
    }

    @Test
    void shouldDeleteUser() {
        User added = userService.createUser(user);
        userService.deleteUser(added.getId());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> userService.getUserById(added.getId()));
        assertEquals("Пользователь с ID " + added.getId() + " не найден.", ex.getMessage());
    }
}