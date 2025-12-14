package ru.yandex.practicum.filmorate.dto;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class Mapper {

    public UserDTO userToDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setLogin(user.getLogin());
        dto.setName(user.getName());
        dto.setBirthday(user.getBirthday());
        return dto;
    }

    public FilmDTO filmToDto(Film film) {
        FilmDTO dto = new FilmDTO();

        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());

        if (Optional.ofNullable(film.getMpa()).isPresent()) {
            dto.setMpa(ratingToDTO(film.getMpa()));
        }

        if (Optional.ofNullable(film.getGenres()).isPresent()) {
            dto.setGenres(film.getGenres().stream()
                    .map(this::genreToDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public GenreDTO genreToDTO(Genre genre) {
        GenreDTO dto = new GenreDTO();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }

    public RatingDTO ratingToDTO(Rating rating) {
        RatingDTO dto = new RatingDTO();
        dto.setId(rating.getId());
        dto.setName(rating.getName());
        return dto;
    }
}
