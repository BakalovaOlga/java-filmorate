package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    private FilmController filmController;
    private Film film;

    @BeforeEach
    public void setUp() {
        filmController = new FilmController();
        film = new Film();
        film.setName("The Devil Wears Prada");
        film.setDescription("Мечтающая стать журналисткой провинциальная девушка Энди " +
                "по окончании университета получает должность помощницы всесильной Миранды Пристли, " +
                "деспотичного редактора одного из крупнейших нью-йоркских журналов мод.");
        film.setReleaseDate(LocalDate.of(2006, 6, 19));
        film.setDuration(109);
    }

    @Test
    public void testAddNewFilm() {
        assertDoesNotThrow(() -> filmController.addFilm(film));
        Collection<Film> films = filmController.getAllFilms();
        assertEquals(1, films.size(), "Некорректное количество фильмов");
    }

    @Test
    void shouldThrowValidationExceptionForTooOldDate() {
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        ValidationException ex = assertThrows(ValidationException.class,
                () -> filmController.addFilm(film));
        assertEquals("Дата релиза не может быть раньше 28.12.1895.", ex.getMessage());
    }

    @Test
    void shouldUpdateExistingFilm() {
        Film added = filmController.addFilm(film);
        added.setName("Дьявол носит Prada (Фильм,2006)");

        Film updated = filmController.updateFilm(added);
        assertEquals("Дьявол носит Prada (Фильм,2006)", updated.getName());
    }
}
