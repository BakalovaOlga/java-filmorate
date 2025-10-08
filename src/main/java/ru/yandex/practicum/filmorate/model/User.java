package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {
    private Long id;
    @Email(message = "E-mail имеет неверный формат.")
    @NotBlank(message = "E-mail не может быть пустым.")
    private String email;
    @NotBlank(message = "Логин не может быть пустым.")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    @NotNull
    private LocalDate birthday;
    private final Set<Long> friends = new HashSet<>();

}