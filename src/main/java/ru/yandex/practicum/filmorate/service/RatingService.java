package ru.yandex.practicum.filmorate.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingService {

    private final RatingStorage dbRatingStorage;

    public Collection<Rating> getAllRatings() {
        log.info("Отправляем запрос на получение списка всех рейтингов ...");
        Collection<Rating> listOfAllRatings = dbRatingStorage.getAllRatings().values();
        log.info("Список рейтингов отправлен клиенту.");
        return listOfAllRatings;
    }

    public Rating getRatingById(@NotNull Long ratingId) {
        log.info("Отправляем запрос на получение информации о рейтинге с id = {} ...", ratingId);
        ratingExistInStorage(ratingId);
        Rating rating = dbRatingStorage.getRatingById(ratingId);
        log.info("Информация о жанре с id {} отправлена клиенту", rating);
        return rating;
    }

    private void ratingExistInStorage(Long ratingId) {
        Optional<Rating> optRating = Optional.ofNullable(dbRatingStorage.getRatingById(ratingId));

        if (optRating.isEmpty()) {
            throw new NotFoundException("Рейтинга с id = " + ratingId + " не существует.");
        }
    }
}
