package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.validation.FilmServiceValidation;
import ru.yandex.practicum.filmorate.service.validation.UserServiceValidation;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmServiceValidation filmServiceValidation;
    private final UserServiceValidation userServiceValidation;
    private final FilmStorage inMemoryFilmStorage;

    public Collection<Film> getAllFilms() {
        log.info("Отправляем запрос на получение списка всех фильмов ...");
        Collection<Film> listOfAllFilms = inMemoryFilmStorage.getAllFilms().values();
        log.info("Список фильмов отправлен клиенту.");
        return listOfAllFilms;
    }

    public Film getFilmById(Long filmId) {
        log.info("Отправляем запрос на получение информации о фильме с id = {} ...", filmId);
        filmServiceValidation.filmExistInStorage(filmId);
        Film film = inMemoryFilmStorage.getFilmById(filmId);
        log.info("Информация о фильме с id = {} отправлена клиенту", filmId);
        return film;
    }

    public List<Film> getTopFilmsByLike(Long count) {
        log.info("Запрашиваем топ-{} фильмов по количеству лайков ...", count);
        List<Film> listTopFilms = inMemoryFilmStorage.getTopFilmsByLikes(count);
        log.info("Список из {} самых популярных фильмов отправлен клиенту", count);
        return listTopFilms;
    }

    public Film create(Film film) {
        log.info("Отправляем запрос на создание фильма ...");
        filmServiceValidation.filmValidationForCreate(film);
        Film newFilm = inMemoryFilmStorage.createFilm(film);
        log.info("Создан фильм с названием: {}", newFilm.getName());
        return newFilm;
    }

    public Film update(Film film) {
        log.info("Отправляем запрос на обновление данных фильма ...");
        filmServiceValidation.filmValidationForUpdate(film);
        Film updateFilm = inMemoryFilmStorage.updateFilm(film);
        log.info("Информация о фильме с id = {} и названием {} обновлена", updateFilm.getId(), updateFilm.getName());
        return updateFilm;
    }

    public void addLikeToFilm(Long filmId, Long userId) {
        log.info("Отправляем запрос на добавление лайка к фильму с id {}", filmId);
        filmServiceValidation.filmExistInStorage(filmId);
        userServiceValidation.userExistInStorage(userId);
        inMemoryFilmStorage.addLikeToFilm(filmId, userId);
        log.info("Пользователь с id = {} поставил лайк фильму с id = {}", userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        log.info("Отправляем запрос на удаление лайка к фильму с id {}", filmId);
        filmServiceValidation.filmExistInStorage(filmId);
        userServiceValidation.userExistInStorage(userId);
        inMemoryFilmStorage.deleteLike(filmId, userId);
        log.info("Пользователь с id = {} удалил лайк у фильма с id = {}", userId, filmId);
    }
}
