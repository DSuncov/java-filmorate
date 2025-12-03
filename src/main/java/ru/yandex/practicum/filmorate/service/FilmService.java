package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.Mapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.validation.UserServiceValidation;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final UserServiceValidation userServiceValidation;
    private final FilmStorage dbFilmStorage;
    private final Mapper mapper;

    public Collection<FilmDTO> getAllFilms() {
        log.info("Отправляем запрос на получение списка всех фильмов ...");
        Collection<FilmDTO> listOfAllFilms = dbFilmStorage.getAllFilms().values()
                .stream()
                .map(mapper::filmToDto)
                .toList();
        log.info("Список фильмов отправлен клиенту.");
        return listOfAllFilms;
    }

    public FilmDTO getFilmById(Long filmId) {
        log.info("Отправляем запрос на получение информации о фильме с id = {} ...", filmId);
        Film film = dbFilmStorage.getFilmById(filmId).get();
        log.info("Информация о фильме с id = {} отправлена клиенту", filmId);
        return mapper.filmToDto(film);
    }

    public List<FilmDTO> getTopFilmsByLike(Long count) {
        log.info("Запрашиваем топ-{} фильмов по количеству лайков ...", count);
        List<FilmDTO> listTopFilms = dbFilmStorage.getTopFilmsByLikes(count)
                .stream()
                .map(mapper::filmToDto)
                .toList();;
        log.info("Список из {} самых популярных фильмов отправлен клиенту", count);
        return listTopFilms;
    }

    public FilmDTO create(Film film) {
        log.info("Отправляем запрос на создание фильма ...");
        Film newFilm = dbFilmStorage.createFilm(film);
        log.info("Создан фильм с названием: {}", newFilm.getName());
        return mapper.filmToDto(newFilm);
    }

    public FilmDTO update(Film film) {
        log.info("Отправляем запрос на обновление данных фильма ...");
        filmExistInStorage(film.getId());
        Film updateFilm = dbFilmStorage.updateFilm(film);
        log.info("Информация о фильме с id = {} и названием {} обновлена", updateFilm.getId(), updateFilm.getName());
        return mapper.filmToDto(updateFilm);
    }

    public void addLikeToFilm(Long filmId, Long userId) {
        log.info("Отправляем запрос на добавление лайка к фильму с id {}", filmId);
        filmExistInStorage(filmId);
        userServiceValidation.userExistInStorage(userId);
        dbFilmStorage.addLikeToFilm(filmId, userId);
        log.info("Пользователь с id = {} поставил лайк фильму с id = {}", userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        log.info("Отправляем запрос на удаление лайка к фильму с id {}", filmId);
        filmExistInStorage(filmId);
        userServiceValidation.userExistInStorage(userId);
        dbFilmStorage.deleteLike(filmId, userId);
        log.info("Пользователь с id = {} удалил лайк у фильма с id = {}", userId, filmId);
    }

    private void filmExistInStorage(Long filmId) {
        if (Optional.ofNullable(dbFilmStorage.getFilmById(filmId)).isEmpty()) {
            throw new EmptyResultDataAccessException("Фильм с id = " + " отсутствует в БД", 0);
        }
    }
}
