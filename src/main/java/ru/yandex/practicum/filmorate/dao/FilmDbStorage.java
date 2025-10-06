package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;


import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.*;

@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         FilmRowMapper filmRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmRowMapper = filmRowMapper;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT f.*, m.rating_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.rating_id = m.rating_id";

        return jdbcTemplate.query(sql, filmRowMapper);
    }

    @Override
    public Film addFilm(Film newFilm) {
        String sql = "INSERT INTO films (film_name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"film_id"});
            ps.setString(1, newFilm.getName());
            ps.setString(2, newFilm.getDescription());
            ps.setDate(3, Date.valueOf(newFilm.getReleaseDate()));
            ps.setInt(4, newFilm.getDuration());

            if (newFilm.getMpa() != null && newFilm.getMpa().getId() != null) {
                ps.setInt(5, newFilm.getMpa().getId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            return ps;
        }, keyHolder);

        Long filmId = getGeneratedId(keyHolder);
        newFilm.setId(filmId);

        return newFilm;
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
        String sql = "UPDATE films SET film_name = ?, description = ?, release_date = ?, " +
                "duration = ?, rating_id = ? WHERE film_id = ?";

        int updated = jdbcTemplate.update(sql,
                newFilm.getName(),
                newFilm.getDescription(),
                Date.valueOf(newFilm.getReleaseDate()),
                newFilm.getDuration(),
                newFilm.getMpa() != null ? newFilm.getMpa().getId() : null,
                newFilm.getId());

        if (updated == 0) {
            throw new NotFoundException("Фильм с id=" + newFilm.getId() + " не найден");
        }
        return newFilm;
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        String sql = "SELECT f.*, m.rating_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.rating_id = m.rating_id " +
                "WHERE f.film_id = ?";

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, id);
        return films.isEmpty() ? Optional.empty() : Optional.of(films.get(0));
    }


    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, m.rating_name, COUNT(l.user_id) as likes_count " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.rating_id = m.rating_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id, m.rating_name " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, filmRowMapper, count);
    }
}

