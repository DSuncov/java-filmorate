package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.validation.FilmServiceValidation;
import ru.yandex.practicum.filmorate.service.validation.UserServiceValidation;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest

public class FilmServiceTest {

    private final UserStorage userStorage = new InMemoryUserStorage();
    private final UserServiceValidation userServiceValidation = new UserServiceValidation(userStorage);

    private final FilmStorage filmStorage = new InMemoryFilmStorage();
    private final FilmServiceValidation filmServiceValidation = new FilmServiceValidation(filmStorage);
    private final FilmService filmService = new FilmService(filmServiceValidation, userServiceValidation, filmStorage);

    @DisplayName("Проверяем метод create() для создания фильма с валидными полями")
    @Test
    void userService_CreateFilm_WithValidFields_Test() {
        Film testFilm1 = new Film("Фильм без названия", "Пустое описание", LocalDate.of(2000, 1, 1), 100L);
        Film testFilm2 = new Film("Новый фильм без названия", "Пустое описание", LocalDate.of(2000, 1, 1), 100L);

        Optional<Film> actualFilm = Optional.ofNullable(filmService.create(testFilm1));
        //Пытаемся добавить фильм с таким же описанием, должно выброситься исключение
        assertThrows(DuplicatedDataException.class, () -> filmService.create(testFilm2));

        assertTrue(actualFilm.isPresent()); // Проверяем, что объект создан
        assertEquals(1, filmStorage.getAllFilms().size());
        assertEquals(actualFilm.get(), filmService.getFilmById(1L)); // Проверяем, что фильм возвращается по id (т.к. добавили только один фильм, то id = 1

        filmStorage.getAllFilms().clear();
    }

    @DisplayName("Проверяем метод update() для обновления фильма с валидными полями")
    @Test
    void userService_UpdateFilm_WithValidFields_Test() {
        Film testFilm1 = new Film("Фильм без названия", "Пустое описание", LocalDate.of(2000, 1, 1), 100L);
        Film testFilmUpdate = new Film("Новый фильм без названия", "Новое описание фильма", LocalDate.of(2000, 1, 1), 100L);

        filmService.create(testFilm1);
        testFilmUpdate.setId(1L); // Устанавливаем id
        Optional<Film> actualFilm = Optional.ofNullable(filmService.update(testFilmUpdate));

        assertTrue(actualFilm.isPresent());
        assertEquals("Новый фильм без названия", actualFilm.get().getName());
        assertEquals("Новое описание фильма", actualFilm.get().getDescription());

        filmStorage.getAllFilms().clear();
    }
}
