package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;


import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final GenreService genreService;
    private final MpaService mpaService;
    private final LikeService likeService;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       UserService userService,
                       GenreService genreService,
                       MpaService mpaService,
                       LikeService likeService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.genreService = genreService;
        this.mpaService = mpaService;
        this.likeService = likeService;
    }

    public Film addFilm(Film newFilm) {
        validateFilm(newFilm);
        mpaService.getMpaById(newFilm.getMpa().getId());
        genreService.validateGenresExist(newFilm.getGenres());

        Film film = filmStorage.addFilm(newFilm);
        genreService.saveFilmGenres(film);
        return film;
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        filmStorage.getFilmById(film.getId());

        mpaService.getMpaById(film.getMpa().getId());
        genreService.validateGenresExist(film.getGenres());

        filmStorage.updateFilm(film);
        genreService.updateFilmGenres(film);
        return getFilmById(film.getId());
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAllFilms();
        genreService.loadFilmsGenres(films);
        likeService.loadFilmsLikes(films);
        return films;
    }

    public Film getFilmById(Long filmId) {
        log.info("Попытка получения фильма по ID: {}", filmId);
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден"));

        Set<Genre> genres = genreService.findGenreByFilmId(filmId);
        film.setGenres(genres);

        Set<Long> likes = likeService.getLikesForFilm(filmId);
        film.getLikes().addAll(likes);

        return film;
    }

    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Количество фильмов должно быть положительным");
        }
        List<Film> films = filmStorage.getPopularFilms(count);

        genreService.loadFilmsGenres(films);
        likeService.loadFilmsLikes(films);

        return films;
    }

    public void addLikeToFilm(Long filmId, Long userId) {
        userService.getUserById(userId);
        likeService.addLike(filmId, userId);
    }

    public void removeLikeFromFilm(Long filmId, Long userId) {
        userService.getUserById(userId);
        likeService.removeLike(filmId, userId);
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895.");
        }
    }

}
