package ru.yandex.practicum.filmorate.dto;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

@Component
public class Mapper {

    public UserDTO userToDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setLogin(user.getLogin());
        dto.setName(user.getName());
        dto.setBirthday(user.getBirthday());
        dto.setFriends(user.getFriends());
        return dto;
    }

    public FilmDTO filmToDto(Film film) {
        FilmDTO dto = new FilmDTO();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setUsersIdWhoLikes(film.getUsersIdWhoLikes());
        return dto;
    }
}
