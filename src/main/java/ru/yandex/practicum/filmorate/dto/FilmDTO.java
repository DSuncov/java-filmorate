package ru.yandex.practicum.filmorate.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class FilmDTO {
    private Long id;
    private String name;
    private String description;
    private List<GenreDTO> genres;
    private RatingDTO mpa;
    private List<DirectorDTO> directors;
    private LocalDate releaseDate;
    private Long duration;
}
