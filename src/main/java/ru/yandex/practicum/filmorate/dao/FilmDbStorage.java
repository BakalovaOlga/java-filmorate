package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.*;

@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         FilmRowMapper filmRowMapper,
                         @Qualifier("genreDbStorage") GenreStorage genreStorage,
                         @Qualifier("mpaDbStorage") MpaStorage mpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmRowMapper = filmRowMapper;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT f.*, m.rating_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.rating_id = m.rating_id";

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);

        films.forEach(genreStorage::loadFilmGenres);
        films.forEach(this::loadFilmLikes);

        return films;
    }

    @Override
    public Film addFilm(Film newFilm) {
        Optional<Integer> ratingId = Optional.ofNullable(newFilm.getMpa())
                .map(Mpa::getId);

        ratingId.ifPresent(mpaStorage::getMpaById);

        Optional.ofNullable(newFilm.getGenres())
                .filter(genres -> !genres.isEmpty())
                .ifPresent(genres -> genres.forEach(genre ->
                        genreStorage.getGenreById(genre.getId())));


        String sql = "INSERT INTO films (film_name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"film_id"});
            ps.setString(1, newFilm.getName());
            ps.setString(2, newFilm.getDescription());
            ps.setDate(3, Date.valueOf(newFilm.getReleaseDate()));
            ps.setInt(4, newFilm.getDuration());

            // Обрабатываем ratingId через Optional
            if (ratingId.isPresent()) {
                ps.setInt(5, ratingId.get());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            return ps;
        }, keyHolder);

        Long filmId = getGeneratedId(keyHolder);
        newFilm.setId(filmId);

        genreStorage.saveFilmGenres(newFilm);

        return getFilmById(filmId);
    }

    private Long getGeneratedId(KeyHolder keyHolder) {
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new DataAccessException("Не удалось получить сгенерированный ID") {
            };
        }
        return key.longValue();
    }

    @Override
    public Film updateFilm(Film newFilm) {
        Optional<Integer> ratingId = Optional.ofNullable(newFilm.getMpa())
                .map(Mpa::getId);
        String sql = "UPDATE films SET film_name = ?, description = ?, release_date = ?, " +
                "duration = ?, rating_id = ? WHERE film_id = ?";

        jdbcTemplate.update(sql,
                newFilm.getName(),
                newFilm.getDescription(),
                Date.valueOf(newFilm.getReleaseDate()),
                newFilm.getDuration(),
                ratingId.orElse(null),
                newFilm.getId());

        genreStorage.updateFilmGenres(newFilm);

        return getFilmById(newFilm.getId());
    }

    @Override
    public Film getFilmById(Long id) {
        String sql = "SELECT f.*, m.rating_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.rating_id = m.rating_id " +
                "WHERE f.film_id = ?";

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, id);

        if (films.isEmpty()) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }

        Film film = films.getFirst();
        genreStorage.loadFilmGenres(film);
        loadFilmLikes(film);

        return film;
    }

    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, m.rating_name, COUNT(l.user_id) as likes_count " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.rating_id = m.rating_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id, m.rating_name " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, count);
        films.forEach(genreStorage::loadFilmGenres);
        films.forEach(this::loadFilmLikes);

        return films;
    }

    //методы для лайков
    public void addLike(Long filmId, Long userId) {
        getFilmById(filmId);

        String checkSql = "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?";
        Integer count;
        try {
            count = jdbcTemplate.queryForObject(checkSql, Integer.class, filmId, userId);
        } catch (DataAccessException e) {
            count = 0;
        }

        if (count != null && count > 0) {
            throw new ValidationException("Лайк можно ставить только один раз");
        }

        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        int deletedRows = jdbcTemplate.update(sql, filmId, userId);

        if (deletedRows == 0) {
            throw new NotFoundException("Лайк пользователя " + userId + " не найден у фильма " + filmId);
        }
    }

    private void loadFilmLikes(Film film) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        Set<Long> likes = new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) ->
                rs.getLong("user_id"), film.getId()));

        film.getLikes().clear();
        film.getLikes().addAll(likes);
    }

}


