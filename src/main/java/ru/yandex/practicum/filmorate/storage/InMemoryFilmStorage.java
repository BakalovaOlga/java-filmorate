package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

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

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
