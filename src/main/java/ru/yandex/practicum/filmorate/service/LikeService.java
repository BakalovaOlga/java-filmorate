package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.LikeStorage;


import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeStorage likeStorage;
    private final UserService userService;

    public void addLike(Long filmId, Long userId) {
        userService.getUserById(userId);
        likeStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        userService.getUserById(userId);
        likeStorage.removeLike(filmId, userId);
    }

    public void removeLikesByFilmId(Long filmId) {
        likeStorage.removeLikesByFilmId(filmId);
    }

    public void removeLikesByUserId(Long userId) {
        userService.getUserById(userId);
        likeStorage.removeLikesByUserId(userId);
    }

    public Set<Long> getLikesForFilm(Long filmId) {
        return likeStorage.getLikesByFilmId(filmId);
    }

    public Map<Long, Set<Long>> getLikesForFilms(List<Long> filmIds) {
        return likeStorage.getLikesByFilmIds(filmIds);
    }

    public void loadFilmsLikes(List<Film> films) {
        if (films.isEmpty()) return;

        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        Map<Long, Set<Long>> likesMap = likeStorage.getLikesByFilmIds(filmIds);

        films.forEach(film -> {
            Set<Long> filmLikes = likesMap.getOrDefault(film.getId(), Set.of());
            film.getLikes().addAll(filmLikes);
        });
    }
}