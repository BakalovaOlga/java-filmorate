package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Repository
@Qualifier("mpaDbStorage")
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaRowMapper mpaRowMapper;

    public MpaDbStorage(JdbcTemplate jdbcTemplate, MpaRowMapper mpaRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaRowMapper = mpaRowMapper;
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT rating_id, rating_name FROM mpa ORDER BY rating_id";
        return jdbcTemplate.query(sql, mpaRowMapper);
    }

    @Override
    public Mpa getMpaById(int id) {
        String sql = "SELECT rating_id, rating_name FROM mpa WHERE rating_id = ?";
        List<Mpa> mpaList = jdbcTemplate.query(sql, mpaRowMapper, id);

        if (mpaList.isEmpty()) {
            throw new NotFoundException("Рейтинг MPA с id=" + id + " не найден"); // ← БРОСАЕМ ИСКЛЮЧЕНИЕ
        }

        return mpaList.getFirst();
    }
}
