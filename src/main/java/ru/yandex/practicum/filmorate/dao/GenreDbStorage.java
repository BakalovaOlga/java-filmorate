package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.*;
import java.util.stream.Collectors;


@Repository
@Qualifier("genreDbStorage")
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;

    public GenreDbStorage(JdbcTemplate jdbcTemplate, GenreRowMapper genreRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreRowMapper = genreRowMapper;
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT genre_id, genre_name FROM genre ORDER BY genre_id";
        return jdbcTemplate.query(sql, genreRowMapper);
    }

    @Override
    public Genre getGenreById(int id) {
        String sql = "SELECT genre_id, genre_name FROM genre WHERE genre_id = ?";
        List<Genre> genreList = jdbcTemplate.query(sql, genreRowMapper, id);

        if (genreList.isEmpty()) {
            throw new NotFoundException("Жанр с id=" + id + " не найден");
        }

        return genreList.getFirst();
    }

    @Override
    public Map<Long, Set<Genre>> getGenresForFilms(List<Long> filmIds) {
        if (filmIds.isEmpty()) {
            return Map.of();
        }

        String sql = "SELECT film_id, genre_id FROM film_genre " +
                "WHERE film_id IN (" +
                filmIds.stream().map(String::valueOf).collect(Collectors.joining(",")) + ") " +
                "ORDER BY film_id, genre_id";

        Map<Long, Set<Genre>> genresMap = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            Long filmId = rs.getLong("film_id");
            Integer genreId = rs.getInt("genre_id");
            genresMap.computeIfAbsent(filmId, k -> new LinkedHashSet<>())
                    .add(new Genre(genreId, null));
        });

        return genresMap;
    }
}
