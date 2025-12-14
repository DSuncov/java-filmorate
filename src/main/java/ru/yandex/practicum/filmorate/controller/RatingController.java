package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.RatingDTO;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @GetMapping("/mpa")
    public ResponseEntity<Collection<RatingDTO>> getAllRating() {
        var listOfAllRatings = ratingService.getAllRatings();
        return ResponseEntity.ok(listOfAllRatings);
    }

    @GetMapping("/mpa/{id}")
    public ResponseEntity<RatingDTO> getRatingById(@PathVariable("id") @NotNull Long ratingId) {
        var rating = ratingService.getRatingById(ratingId);
        return ResponseEntity.ok(rating);
    }
}
