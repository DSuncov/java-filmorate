package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/genres")
    public ResponseEntity<Collection<GenreDTO>> getAllGenres() {
        var listOfAllGenres = genreService.getAllGenres();
        return ResponseEntity.ok(listOfAllGenres);
    }

    @GetMapping("/genres/{id}")
    public ResponseEntity<GenreDTO> getGenreById(@PathVariable("id") @NotNull Long genreId) {
        var genre = genreService.getGenreById(genreId);
        return ResponseEntity.ok(genre);
    }
}
