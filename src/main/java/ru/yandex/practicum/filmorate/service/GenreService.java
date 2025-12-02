package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreService {

    private final GenreStorage dbGenreStorage;

    public Collection<Genre> getAllGenres() {
        log.info("Отправляем запрос на получение списка всех жанров ...");
        Collection<Genre> listOfAllGenres = dbGenreStorage.getAllGenres().values();
        log.info("Список жанров отправлен клиенту.");
        return listOfAllGenres;
    }

    public Genre getGenreById(Long genreId) {
        log.info("Отправляем запрос на получение информации о жанре с id = {} ...", genreId);
        genreExistInStorage(genreId);
        Genre genre = dbGenreStorage.getGenreById(genreId);
        log.info("Информация о жанре с id {} отправлена клиенту", genre);
        return genre;
    }

    private void genreExistInStorage(Long genreId) {
        Optional<Genre> optGenre = Optional.ofNullable(dbGenreStorage.getGenreById(genreId));

        if (optGenre.isEmpty()) {
            throw new NotFoundException("Жанра с id = " + genreId + " не существует.");
        }
    }
}
