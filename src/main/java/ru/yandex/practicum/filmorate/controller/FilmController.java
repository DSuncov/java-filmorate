package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @GetMapping("/films")
    public ResponseEntity<Collection<FilmDTO>> getAllFilms() {
        var listOfAllFilms = filmService.getAllFilms();
        return ResponseEntity.ok(listOfAllFilms);
    }

    @GetMapping("films/director/{directorId}")
    public ResponseEntity<Collection<FilmDTO>> getAllFilmsByDirectorAndSortedBy(
            @PathVariable("directorId") @NotNull(message = "id режиссера должно быть указано") Long directorId,
            @RequestParam("sortBy") String sortRule
    ) {
        var listOfFilmsByDirector = filmService.getAllFilmsByDirectorAndSortedBy(directorId, sortRule);
        return ResponseEntity.ok(listOfFilmsByDirector);
    }

    @GetMapping("/films/{id}")
    public ResponseEntity<FilmDTO> getFilmById(@PathVariable("id") @NotNull(message = "id фильма должно быть задано") Long filmId) {
        var film = filmService.getFilmById(filmId);
        return ResponseEntity.ok(film);
    }

    @GetMapping("/films/popular")
    public ResponseEntity<Collection<FilmDTO>> getTopFilms(@RequestParam(defaultValue = "10") Long count) {
        var listOfTopFilms = filmService.getTopFilmsByLike(count);
        return ResponseEntity.ok(listOfTopFilms);
    }

    @PostMapping("/films")
    public ResponseEntity<FilmDTO> createFilm(@NotNull(message = "Передано пустое значение Film") @Valid @RequestBody Film film) {
        var newFilm = filmService.create(film);
        return ResponseEntity.status(HttpStatus.CREATED).body(newFilm);
    }

    @PutMapping("/films")
    public ResponseEntity<FilmDTO> update(@NotNull(message = "Передано пустое значение Film") @Valid @RequestBody Film film) {
        var updateFilm = filmService.update(film);
        return ResponseEntity.ok(updateFilm);
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
