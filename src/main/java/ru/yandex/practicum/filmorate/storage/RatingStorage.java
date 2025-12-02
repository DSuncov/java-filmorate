package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Map;

public interface RatingStorage {

    Map<Long, Rating> getAllRatings();

    Rating getRatingById(Long ratingId);
}
