package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.validation.FilmServiceValidation;
import ru.yandex.practicum.filmorate.service.validation.UserServiceValidation;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmServiceValidation filmServiceValidation;
    private final UserServiceValidation userServiceValidation;
    private final FilmStorage inMemoryFilmStorage;

    public Collection<Film> getAllFilms() {
        return inMemoryFilmStorage.getAllFilms().values();
    }

    public Film getFilmById(Long id) {
        filmServiceValidation.filmExistInStorage(id);
        return inMemoryFilmStorage.getFilmById(id);
    }

    public List<Film> getTopFilmsByLike(Long count) {
        return inMemoryFilmStorage.getTopFilmsByLikes(count);
    }

    public Film create(Film film) {
        filmServiceValidation.filmValidationForCreate(film);
        return inMemoryFilmStorage.createFilm(film);
    }

    public Film update(Film film) {
        filmServiceValidation.filmValidationForUpdate(film);
        return inMemoryFilmStorage.updateFilm(film);
    }

    public void addLikeToFilm(Long filmId, Long userId) {
        filmServiceValidation.filmExistInStorage(filmId);
        userServiceValidation.userExistInStorage(userId);
        inMemoryFilmStorage.addLikeToFilm(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        filmServiceValidation.filmExistInStorage(filmId);
        userServiceValidation.userExistInStorage(userId);
        inMemoryFilmStorage.deleteLike(filmId, userId);
    }
}
