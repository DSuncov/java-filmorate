package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;

public interface DirectorStorage {

    Map<Long, Director> getAllDirectors();

    Director getDirectorById(Long directorId);

    List<Director> getDirectorsByFilmId(Long filmId);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(Long directorId);
}
