package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@Slf4j
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(@Qualifier("mpaDbStorage") MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> getAllMpa() {
        log.info("Получение всех рейтингов MPA");
        return mpaStorage.getAllMpa();
    }

    public Mpa getMpaById(int id) {
        log.info("Получение рейтинга MPA с ID: {}", id);

        if (id <= 0) {
            throw new ValidationException("ID рейтинга MPA должен быть положительным");
        }

        Mpa mpa = mpaStorage.getMpaById(id);
        log.info("Найден рейтинг MPA: {}", mpa.getName());
        return mpa;
    }
}
