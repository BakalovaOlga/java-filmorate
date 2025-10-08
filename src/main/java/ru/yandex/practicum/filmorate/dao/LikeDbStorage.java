package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLikesByFilmId(Long filmId) {
        String sql = "DELETE FROM likes WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public void removeLikesByUserId(Long userId) {
        String sql = "DELETE FROM likes WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    @Override
    public Set<Long> getLikesByFilmId(Long filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Long> likes = jdbcTemplate.queryForList(sql, Long.class, filmId);
        return new HashSet<>(likes);
    }

    @Override
    public Map<Long, Set<Long>> getLikesByFilmIds(List<Long> filmIds) {
        if (filmIds.isEmpty()) return Map.of();

        String sql = "SELECT film_id, user_id FROM likes WHERE film_id IN (" +
                filmIds.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";

        Map<Long, Set<Long>> likesMap = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            Long filmId = rs.getLong("film_id");
            Long userId = rs.getLong("user_id");
            likesMap.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        });

        return likesMap;
    }
}