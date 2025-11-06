package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.annotations.MinReleaseDate;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

/**
 * Film.
 */
@Data
@EqualsAndHashCode(exclude = {"releaseDate", "duration"})
public class Film {

    private Long id;

    @NotBlank(message = "Название фильма должно быть заполнено")
    private String name;

    @NotBlank(message = "Описание не должно быть пустым")
    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    private String description;

    @NotNull(message = "Дата выхода фильма должна быть задана.")
    @MinReleaseDate
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма не должна быть отрицательным числом")
    private Long duration;

    private Set<Long> usersIdWhoLikes = new TreeSet<>();

    public Film(String name, String description, LocalDate releaseDate, Long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
