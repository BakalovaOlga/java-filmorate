package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
public class FilmControllerTest {

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    @Autowired
    private GenreService genreService;

    @Autowired
    private MpaService mpaService;

    private Film film;

    @BeforeEach
    void setUp() {
        // Очищаем базу перед каждым тестом
        filmService.getAllFilms().forEach(f -> filmService.deleteFilm(f.getId()));
        userService.getAllUsers().forEach(u -> userService.deleteUser(u.getId()));

        film = new Film();
        film.setName("The Devil Wears Prada");
        film.setDescription("Мечтающая стать журналисткой провинциальная девушка Энди "
                + "по окончании университета получает должность помощницы.");
        film.setReleaseDate(LocalDate.of(2006, 6, 19));
        film.setDuration(109);
        film.setMpa(new Mpa(1, null)); // Указываем существующий MPA ID
        film.setGenres(Set.of()); // Пустой набор жанров
    }

    @Test
    public void testAddNewFilm() {
        assertDoesNotThrow(() -> filmService.addFilm(film));
        List<Film> films = filmService.getAllFilms();
        assertEquals(1, films.size(), "Некорректное количество фильмов");
    }

    @Test
    void shouldThrowValidationExceptionForTooOldDate() {
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        ValidationException ex = assertThrows(ValidationException.class,
                () -> filmService.addFilm(film));
        assertEquals("Дата релиза не может быть раньше 28.12.1895.", ex.getMessage());
    }

    @Test
    void shouldUpdateExistingFilm() {
        Film added = filmService.addFilm(film);
        added.setName("Дьявол носит Prada (Фильм,2006)");

        Film updated = filmService.updateFilm(added);
        assertEquals("Дьявол носит Prada (Фильм,2006)", updated.getName());
    }

    @Test
    void shouldGetFilmById() {
        Film added = filmService.addFilm(film);
        Film found = filmService.getFilmById(added.getId());

        assertEquals(added.getId(), found.getId());
        assertEquals("The Devil Wears Prada", found.getName());
    }

    @Test
    void shouldDeleteFilm() {
        Film added = filmService.addFilm(film);
        filmService.deleteFilm(added.getId());

        List<Film> films = filmService.getAllFilms();
        assertEquals(0, films.size());
    }
}