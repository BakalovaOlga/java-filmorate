package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Qualifier("inMemoryMpaStorage")
public class InMemoryMpaStorage implements MpaStorage {
    private final Map<Integer, Mpa> mpaRatings = new HashMap<>();

    public InMemoryMpaStorage() {
        // Инициализация тестовыми MPA рейтингами
        mpaRatings.put(1, new Mpa(1, "G"));
        mpaRatings.put(2, new Mpa(2, "PG"));
        mpaRatings.put(3, new Mpa(3, "PG-13"));
        mpaRatings.put(4, new Mpa(4, "R"));
        mpaRatings.put(5, new Mpa(5, "NC-17"));
    }

    @Override
    public List<Mpa> getAllMpa() {
        return new ArrayList<>(mpaRatings.values());
    }

    @Override
    public Mpa getMpaById(int id) {
        Mpa mpa = mpaRatings.get(id);
        if (mpa == null) {
            throw new RuntimeException("MPA рейтинг с id=" + id + " не найден");
        }
        return mpa;
    }
}