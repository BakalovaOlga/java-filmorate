package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;

    @Size(max = 200, message = "Описание должно быть не больше 200 символов.")
    private String description;
    @NotNull
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма не может быть отрицательной.")
    private Integer duration;

    private final Set<Long> likes = new HashSet<>();

    private Mpa mpa;

    private Set<Genre> genres;
}
