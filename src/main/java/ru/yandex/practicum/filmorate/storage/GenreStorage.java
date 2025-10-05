package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    List<Genre> getAllGenres();

    Genre getGenreById(int id);

    void loadFilmGenres(Film film);

    void saveFilmGenres(Film film);

    void updateFilmGenres(Film film);
}
