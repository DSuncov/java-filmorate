package ru.yandex.practicum.filmorate.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.Mapper;
import ru.yandex.practicum.filmorate.dto.RatingDTO;
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
    private final Mapper mapper;

    public Collection<RatingDTO> getAllRatings() {
        log.info("Отправляем запрос на получение списка всех рейтингов ...");
        Collection<RatingDTO> listOfAllRatings = dbRatingStorage.getAllRatings().values()
                .stream()
                .map(mapper::ratingToDTO)
                .toList();
        log.info("Список рейтингов отправлен клиенту.");
        return listOfAllRatings;
    }

    public RatingDTO getRatingById(@NotNull Long ratingId) {
        log.info("Отправляем запрос на получение информации о рейтинге с id = {} ...", ratingId);
        ratingExistInStorage(ratingId);
        Rating rating = dbRatingStorage.getRatingById(ratingId);
        log.info("Информация о жанре с id {} отправлена клиенту", rating);
        return mapper.ratingToDTO(rating);
    }

    private void ratingExistInStorage(Long ratingId) {
        Optional<Rating> optRating = Optional.ofNullable(dbRatingStorage.getRatingById(ratingId));

        if (optRating.isEmpty()) {
            throw new NotFoundException("Рейтинга с id = " + ratingId + " не существует.");
        }
    }
}
