package ru.yandex.practicum.filmorate;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmServiceTest {
    final FilmService filmService = new FilmService();
    final FilmController filmController = new FilmController(filmService);

    final List<Film> films = List.of(
            new Film("Film1", "FilmDescription1", null, null),
            new Film("Film2", "FilmDescription2", null, null),
            new Film("Film3", "FilmDescription3", null, null),
            new Film("Film4", "FilmDescription4", null, null),
            new Film("Film5", "FilmDescription5", null, null),
            new Film("Film6", "FilmDescription6", null, null),
            new Film("Film7", "FilmDescription7", null, null),
            new Film("Film8", "FilmDescription8", null, null),
            new Film("Film9", "FilmDescription9", null, null),
            new Film("Film10", "FilmDescription10", null, null));

    @BeforeEach
    public void createFilmsForTesting() {
        for (Film film : films) {
            filmController.createFilm(film);
        }
    }

    @AfterEach
    public void clear() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        filmService.getMap().clear();
    }

    @DisplayName("Проверяет размер коллекции, которую возвращает метод getAllFilms")
    @Test
    void userController_Return_Correct_Numbers_Of_Films() {
        int expectedSize = 10;
        assertEquals(expectedSize, filmController.getAllFilms().size());
    }

    @DisplayName("Возвращает фильм по id")
    @Test
    void userController_Return_Correct_Film_By_Id() {
        Film expectedUserById1 = films.getFirst();
        Film expectedUserById5 = films.get(4);
        Film expectedUserById10 = films.get(9);

        assertEquals(expectedUserById1, filmController.getFilmById(1L));
        assertEquals(expectedUserById5, filmController.getFilmById(5L));
        assertEquals(expectedUserById10, filmController.getFilmById(10L));
        assertThrows(ValidationException.class, () -> filmController.getFilmById(11L));
        assertThrows(ValidationException.class, () -> filmController.getFilmById(16L));
        assertThrows(ValidationException.class, () -> filmController.getFilmById(0L));
    }

}
