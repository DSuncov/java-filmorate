package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;

public interface GenreStorage {

    Map<Long, Genre> getAllGenres();

    Genre getGenreById(Long genreId);

    List<Genre> getGenresByFilmId(Long filmId);
}
