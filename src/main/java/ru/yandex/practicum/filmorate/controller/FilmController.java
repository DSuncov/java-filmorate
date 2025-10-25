package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAllFilms () {
        Collection<Film> listOfFilms = filmService.getAllFilms();
        log.info("Список фильмов отправлен");
        return listOfFilms;
    }

    @GetMapping(path = {"id"})
    public Film getFilmById(@PathVariable("id") Long filmId) {
        Film film = filmService.getFilmById(filmId);
        log.info("Инфопмация о фильме с id = {} отправлена", filmId);
        return film;
    }

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) {
        Film newFilm = filmService.create(film);
        log.info("Создан фильм с названием: {}", film.getName());
        return newFilm;
    }

    @PutMapping("{id}")
    public Film updateFilm(@PathVariable("id") @Min(1) Long filmId, @Valid @RequestBody Film film) {
        Film updateFilm = filmService.update(filmId, film);
        log.info("Информация о фильме с id = {} и названием {} обновлена", filmId, film.getName());
        return updateFilm;
    }
}
