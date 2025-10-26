package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Validated
public class FilmService {
    private final Map<Long, Film> films = new HashMap<>(); // храним информацию обо всех фильмах

    public Map<Long, Film> getMap() {
        return films;
    }

    public Collection<Film> getAllFilms() {
        return films.values();
    }

    public Film getFilmById(Long id) {
        Optional<Film> optionalFilm = Optional.ofNullable(films.get(id));
        return optionalFilm.orElseThrow(() -> new ValidationException("Фильма с id = " + id + " не существует"));
    }

    public Film create(Film film) {
        String description = film.getDescription();
        Optional<String> findDescription = findDescription(description);
        if (findDescription.isPresent()) {
            throw new ValidationException("Описание фильма совпадает с существующим в базе");
        }

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        film.setId(getFilmId());
        films.put(film.getId(), film);
        return film;
    }

    public Film update(Film film) {
        Film updateFilm;
        if (film != null && film.getId() != null) {
            updateFilm = update(film.getId(), film);
        } else {
            throw new ValidationException("Для обновления фильма задайте id");
        }
        return updateFilm;
    }

    public Film update(Long id, Film film) {
        Film oldFilmData = getFilmById(id);

        if (film == null) {
            return oldFilmData;
        }

        String name = film.getName();
        if (name != null && !name.equals(oldFilmData.getName())) {
            oldFilmData.setName(name);
        }

        String description = film.getDescription();
        if (description != null && !description.isBlank() && !description.equals(oldFilmData.getDescription())) {
            if (findDescription(description).isPresent()) {
                throw new ValidationException("Описания совпадают");
            }
            oldFilmData.setDescription(description);
        }

        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate != null && releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        oldFilmData.setReleaseDate(releaseDate);

        Long duration = film.getDuration();
        if (duration != null && !duration.equals(oldFilmData.getDuration())) {
            oldFilmData.setDuration(duration);
        }
        return oldFilmData;
    }

    private long getFilmId() {
        long currentMaxId = films.values().stream()
                .map(Film::getId)
                .max(Long::compare)
                .orElse(0L);
        return ++currentMaxId;
    }

    private Optional<String> findDescription(String description) {
        return films.values().stream()
                .map(Film::getDescription)
                .filter(u -> u.equals(description))
                .findFirst();
    }
}
