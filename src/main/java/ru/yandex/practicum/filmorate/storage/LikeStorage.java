package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface LikeStorage {
    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    void removeLikesByFilmId(Long filmId);

    void removeLikesByUserId(Long userId);

    Set<Long> getLikesByFilmId(Long filmId);

    Map<Long, Set<Long>> getLikesByFilmIds(List<Long> filmIds);
}