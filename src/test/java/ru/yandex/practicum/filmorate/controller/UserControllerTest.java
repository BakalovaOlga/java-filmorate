package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;
    private User user;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        user = new User(1L, "hello@email.com", "neo", "Neo",
                LocalDate.of(1990, 2, 6));
    }

    @Test
    void shouldAdduser() {
        User added = userController.createUser(user);
        assertNotNull(added.getId());
        assertEquals("Neo", added.getName());
    }

    @Test
    void shouldSetNameToLoginIfNameIsNull() {
        user.setName(null);
        User added = userController.createUser(user);
        assertEquals("neo", added.getName());
    }

    @Test
    void shouldSetNameToLoginIfNameIsBlank() {
        user.setName(" ");
        User added = userController.createUser(user);
        assertEquals("neo", added.getName());
    }

    @Test
    void shouldUpdateExistingUser() {
        User added = userController.createUser(user);
        added.setName("Нео");
        User updated = userController.updateUser(added);

        assertEquals("Нео", updated.getName());
        assertEquals(added.getId(), updated.getId());
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingNonExistentUser() {
        user.setId(999L); // Несуществующий ID
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> userController.updateUser(user));
        assertEquals("Пользователь с ID 999 не найден.", ex.getMessage());
    }

    @Test
    void shouldReturnAllUsers() {
        userController.createUser(user);
        Collection<User> users = userController.getAllUsers();
        assertEquals(1, users.size());
    }
}