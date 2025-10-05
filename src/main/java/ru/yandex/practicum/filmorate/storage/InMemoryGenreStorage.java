package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository
@Qualifier("inMemoryGenreStorage")
public class InMemoryGenreStorage implements GenreStorage {
    private final Map<Integer, Genre> genres = new HashMap<>();

    public InMemoryGenreStorage() {
        // Инициализация тестовыми жанрами
        genres.put(1, new Genre(1, "Комедия"));
        genres.put(2, new Genre(2, "Драма"));
        genres.put(3, new Genre(3, "Мультфильм"));
        genres.put(4, new Genre(4, "Триллер"));
        genres.put(5, new Genre(5, "Документальный"));
        genres.put(6, new Genre(6, "Боевик"));
    }

    @Override
    public List<Genre> getAllGenres() {
        return new ArrayList<>(genres.values());
    }

    @Override
    public Genre getGenreById(int id) {
        Genre genre = genres.get(id);
        if (genre == null) {
            throw new RuntimeException("Жанр с id=" + id + " не найден");
        }
        return genre;
    }

    @Override
    public void loadFilmGenres(Film film) {

        film.setGenres(new HashSet<>());
    }

    @Override
    public void saveFilmGenres(Film film) {

    }

    @Override
    public void updateFilmGenres(Film film) {

    }
}