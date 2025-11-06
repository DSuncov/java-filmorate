package ru.yandex.practicum.filmorate.service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FilmServiceValidation {

    private final FilmStorage inMemoryFilmStorage;

    /*
    В контроллере с помощью аннотации проверили , что Film не null и что поля валидны.
    Остается проверить, что фильма с таким description нет в базе (по идее не могут быть 2 фильма с одинаковым описанием,
    а с названием, дато релиза и продолжительностью могут быть).
     */
    public void filmValidationForCreate(Film film) {
        descriptionValidation(film);
    }

    /*
    В контроллере с помощью аннотации проверили , что Film не null и что поля валидны.
    Остается проверить, что фильм с переданным в запросе id есть в базе.
    Далее проверяем, что новый description не используется и не равен существующему.
     */
    public void filmValidationForUpdate(Film film) {
        filmExistInStorage(film.getId());
        Film filmOldData = inMemoryFilmStorage.getFilmById(film.getId());
        if (!film.getDescription().equals(filmOldData.getDescription())) {
            descriptionValidation(film);
        }
    }

    public void filmExistInStorage(Long filmId) {
        if (!inMemoryFilmStorage.getAllFilms().containsKey(filmId)) {
            throw new NotFoundException("Фильма с id = " + filmId + " не существует.");
        }
    }

    private void descriptionValidation(Film film) {
        String description = film.getDescription();
        Optional<String> findDescription = inMemoryFilmStorage.getAllFilms().values().stream()
                .map(Film::getDescription)
                .filter(u -> u.equals(description))
                .findFirst();
        if (findDescription.isPresent()) {
            throw new DuplicatedDataException("Описание фильма совпадает с существующим в базе.");
        }
    }
}
