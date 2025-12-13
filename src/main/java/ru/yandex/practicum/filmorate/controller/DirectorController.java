package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorDTO;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Validated
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping("/directors")
    public ResponseEntity<Collection<DirectorDTO>> getAllDirectors() {
        var listOfAllDirectors = directorService.getAllDirectors();
        return ResponseEntity.ok(listOfAllDirectors);
    }

    @GetMapping("/directors/{id}")
    public ResponseEntity<DirectorDTO> getDirectorById(@PathVariable("id") @NotNull(message = "id режиссера должно быть указано") Long directorId) {
        var director = directorService.getDirectorById(directorId);
        return ResponseEntity.ok(director);
    }

    @PostMapping("/directors")
    public ResponseEntity<DirectorDTO> createDirector(@NotNull(message = "Передано пустое значение Director") @RequestBody Director director) {
        var newDirector = directorService.createDirector(director);
        return ResponseEntity.status(HttpStatus.CREATED).body(newDirector);
    }

    @PutMapping("/directors")
    public ResponseEntity<DirectorDTO> updateDirector(@NotNull(message = "Передано пустое значение Director") @RequestBody Director director) {
        var updatedDirector = directorService.updateDirector(director);
        return ResponseEntity.ok(updatedDirector);
    }

    @DeleteMapping("/directors/{id}")
    public void deleteDirector(@PathVariable("id") @NotNull(message = "id режиссера должно быть указано") Long directorId) {
        directorService.deleteDirector(directorId);
    }
}
