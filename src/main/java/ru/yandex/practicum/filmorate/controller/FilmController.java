package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping("/films")
    public Collection<Film> getAllFilms() {
        log.info("Отправляем запрос на получение списка всех фильмов ...");
        Collection<Film> listOfFilms = filmService.getAllFilms();
        log.info("Список фильмов отправлен клиенту.");
        return listOfFilms;
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable("id") @NotNull(message = "id фильма должно быть задано") Long filmId) {
        log.info("Отправляем запрос на получение информации о фильме с id = {} ...", filmId);
        Film film = filmService.getFilmById(filmId);
        log.info("Информация о фильме с id = {} отправлена клиенту", filmId);
        return film;
    }

    @GetMapping("/films/popular")
    public Collection<Film> getTopFilms(@RequestParam(defaultValue = "10") Long count) {
        log.info("Ищем топ-{} фильмов по количеству лайков ...", count);
        Collection<Film> listPopularFilms = filmService.getTopFilmsByLike(count);
        log.info("Список из {} самых популярных фильмов отправлен клиенту", count);
        return listPopularFilms;
    }

    @PostMapping("/films")
    public Film createFilm(@NotNull(message = "Передано пустое значение Film") @Valid @RequestBody Film film) {
        log.info("Отправляем запрос на создание фильма ...");
        Film newFilm = filmService.create(film);
        log.info("Создан фильм с названием: {}", film.getName());
        return newFilm;
    }

    @PutMapping("/films")
    public Film update(@NotNull(message = "Передано пустое значение Film") @Valid @RequestBody Film film) {
        log.info("Отправляем запрос на обновление данных фильма ...");
        Film updateFilm = filmService.update(film);
        log.info("Информация о фильме с id = {} и названием {} обновлена", film.getId(), film.getName());
        return updateFilm;
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLikeToFilm(@PathVariable("id") @NotNull(message = "id фильма должно быть задано") Long filmId,
                              @PathVariable("userId") @NotNull(message = "id пользователя должно быть задано") Long userId) {
        log.info("Отправляем запрос на добавление лайка к фильму с id {}", filmId);
        filmService.addLikeToFilm(filmId, userId);
        log.info("Пользователь с id = {} поставил лайк фильму с id = {}", userId, filmId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") @NotNull(message = "id фильма должно быть задано") Long filmId,
                           @PathVariable("userId") @NotNull(message = "id пользователя должно быть задано") Long userId) {
        log.info("Отправляем запрос на удаление лайка к фильму с id {}", filmId);
        filmService.deleteLike(filmId, userId);
        log.info("Пользователь с id = {} удалил лайк у фильма с id = {}", userId, filmId);
    }
}
