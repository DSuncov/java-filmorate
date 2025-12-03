package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.dto.Mapper;
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
    private final Mapper mapper;

    public Collection<GenreDTO> getAllGenres() {
        log.info("Отправляем запрос на получение списка всех жанров ...");
        Collection<GenreDTO> listOfAllGenres = dbGenreStorage.getAllGenres().values()
                .stream()
                .map(mapper::genreToDTO)
                .toList();
        log.info("Список жанров отправлен клиенту.");
        return listOfAllGenres;
    }

    public GenreDTO getGenreById(Long genreId) {
        log.info("Отправляем запрос на получение информации о жанре с id = {} ...", genreId);
        genreExistInStorage(genreId);
        Genre genre = dbGenreStorage.getGenreById(genreId);
        log.info("Информация о жанре с id {} отправлена клиенту", genre);
        return mapper.genreToDTO(genre);
    }

    private void genreExistInStorage(Long genreId) {
        Optional<Genre> optGenre = Optional.ofNullable(dbGenreStorage.getGenreById(genreId));

        if (optGenre.isEmpty()) {
            throw new NotFoundException("Жанра с id = " + genreId + " не существует.");
        }
    }
}
