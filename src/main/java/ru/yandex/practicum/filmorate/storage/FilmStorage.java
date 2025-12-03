package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {

    Map<Long, Film> getAllFilms();

    Optional<Film> getFilmById(Long filmId);

    List<Film> getTopFilmsByLikes(Long count);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void addLikeToFilm(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);
}
