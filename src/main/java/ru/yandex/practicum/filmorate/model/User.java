package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.annotations.NotWhiteSpace;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

/**
 * User
 */
@Data
@EqualsAndHashCode(exclude = {"name", "birthday"})
public class User {

    private Long id;

    @NotBlank(message = "E-mail не может быть пустым")
    @Email(message = "Указан некорректный E-mail.")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @NotWhiteSpace
    private String login;

    private String name;

    @NotNull
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    private Set<Long> friends = new TreeSet<>();

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
