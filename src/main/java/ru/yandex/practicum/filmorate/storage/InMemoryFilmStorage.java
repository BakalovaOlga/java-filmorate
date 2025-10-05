package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Integer, Mpa> mpaRatings = Map.of(
            1, new Mpa(1, "G"),
            2, new Mpa(2, "PG"),
            3, new Mpa(3, "PG-13"),
            4, new Mpa(4, "R"),
            5, new Mpa(5, "NC-17")
    );
    private final Map<Integer, Genre> genres = Map.of(
            1, new Genre(1, "Комедия"),
            2, new Genre(2, "Драма"),
            3, new Genre(3, "Мультфильм"),
            4, new Genre(4, "Триллер"),
            5, new Genre(5, "Документальный"),
            6, new Genre(6, "Боевик")
    );

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с ID:" + id + " не найден");
        }
        return films.get(id);
    }

    @Override
    public Film addFilm(Film newFilm) {
        newFilm.setId(getNextId());
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм с ID {} успешно добавлен", newFilm.getId());
        return newFilm;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Фильм с ID {} успешно обновлен", film.getName());
            return film;
        }

        log.debug("Фильм с ID {} не найден", film.getId());
        throw new NotFoundException("Фильм с ID:" + film.getId() + " не найден");
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);

        if (film.getLikes().contains(userId)) {
            throw new ValidationException("Лайк можно ставить только один раз");
        }

        film.getLikes().add(userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);

        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("Лайк пользователя " + userId + " не найден у фильма " + filmId);
        }

        film.getLikes().remove(userId);
        log.info("Пользователь {} удалил лайк с фильма {}", userId, filmId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            return List.of();
        }

        return films.values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
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
