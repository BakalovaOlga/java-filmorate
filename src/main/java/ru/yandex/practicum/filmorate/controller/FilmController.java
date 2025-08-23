package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film newFilm) {
        validateFilm(newFilm);
        newFilm.setId(getNextId());
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм с ID {} успешно добавлен", newFilm.getId());
        return newFilm;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("Id обновляемого фильма не задан.");
        }

        if (!films.containsKey(newFilm.getId())) {
            throw new NotFoundException("Фильм с ID " + newFilm.getId() + " не найден.");
        } else {
            validateFilm(newFilm);
            films.put(newFilm.getId(), newFilm);
            log.info("Фильм с ID {} успешно обновлен", newFilm.getId());
            return newFilm;
        }
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895.");
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
