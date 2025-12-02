package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.Mapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private final Mapper mapper;

    @GetMapping("/films")
    public ResponseEntity<Collection<FilmDTO>> getAllFilms() {
        var listOfAllFilms = filmService.getAllFilms()
                .stream()
                .map(mapper::filmToDto)
                .toList();
        return ResponseEntity.ok(listOfAllFilms);
    }

    @GetMapping("/films/{id}")
    public ResponseEntity<FilmDTO> getFilmById(@PathVariable("id") @NotNull(message = "id фильма должно быть задано") Long filmId) {
        var film = filmService.getFilmById(filmId);
        return ResponseEntity.ok(mapper.filmToDto(film));
    }

    @GetMapping("/films/popular")
    public ResponseEntity<Collection<FilmDTO>> getTopFilms(@RequestParam(defaultValue = "10") Long count) {
        var listOfTopFilms = filmService.getTopFilmsByLike(count)
                .stream()
                .map(mapper::filmToDto)
                .toList();
        return ResponseEntity.ok(listOfTopFilms);
    }

    @PostMapping("/films")
    public ResponseEntity<FilmDTO> createFilm(@NotNull(message = "Передано пустое значение Film") @Valid @RequestBody Film film) {
        var newFilm = filmService.create(film);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.filmToDto(newFilm));
    }

    @PutMapping("/films")
    public ResponseEntity<FilmDTO> update(@NotNull(message = "Передано пустое значение Film") @Valid @RequestBody Film film) {
        var updateFilm = filmService.update(film);
        return ResponseEntity.ok(mapper.filmToDto(updateFilm));
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLikeToFilm(@PathVariable("id") @NotNull(message = "id фильма должно быть задано") Long filmId,
                              @PathVariable("userId") @NotNull(message = "id пользователя должно быть задано") Long userId) {
        filmService.addLikeToFilm(filmId, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") @NotNull(message = "id фильма должно быть задано") Long filmId,
                           @PathVariable("userId") @NotNull(message = "id пользователя должно быть задано") Long userId) {
        filmService.deleteLike(filmId, userId);
    }
}
