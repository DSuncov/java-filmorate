package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.validation.UserServiceValidation;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    private final UserStorage userStorage = new InMemoryUserStorage();
    private final UserServiceValidation userServiceValidation = new UserServiceValidation(userStorage);
    private final UserService userService = new UserService(userStorage, userServiceValidation);

    @DisplayName("Проверяем метод create() для создания пользователя с валидными полями")
    @Test
    void userService_CreateUser_WithValidFields_Test() {
        User testUser1 = new User("testUser1email@yandex.ru", "LOGIN", "", LocalDate.of(2000, 1, 1));
        User testUser2 = new User("testUser1email@yandex.ru", "login", "", LocalDate.of(2000, 1, 1));
        User testUser3 = new User("testUser3email@yandex.ru", "LOGIN", "", LocalDate.of(2000, 1, 1));

        Optional<User> actualUser = Optional.ofNullable(userService.create(testUser1));

        //Пытаемся добавить пользователя с такой же почтой и логином, должно выброситься исключение
        assertThrows(DuplicatedDataException.class, () -> userService.create(testUser2));
        assertThrows(DuplicatedDataException.class, () -> userService.create(testUser3));

        assertTrue(actualUser.isPresent()); // Проверяем, что объект создан
        assertEquals(1, userStorage.getAllUsers().size());
        assertEquals("LOGIN", actualUser.get().getName()); // Проверяем, что имя пользователя устанавливается как в логине
        assertEquals(actualUser.get(), userService.getUserById(1L)); // Проверяем, что пользователь возвращается по id (т.к. добавили только одного пользователя, то id = 1

        userStorage.getAllUsers().clear();
    }

    @DisplayName("Проверяем метод update() для обновления пользователя с валидными полями")
    @Test
    void userService_UpdateUser_WithValidFields_Test() {
        User testUser = new User("testUser1email@yandex.ru", "LOGIN", "", LocalDate.of(2000, 1, 1));
        userService.create(testUser);
        User testUserUpdate = new User("newemail@yandex.ru", "new_login", "", LocalDate.of(2000, 1, 1));
        testUserUpdate.setId(1L); // Устанавливаем id
        Optional<User> actualUser = Optional.ofNullable(userService.update(testUserUpdate));

        assertTrue(actualUser.isPresent());
        assertEquals("LOGIN", actualUser.get().getName()); // Проверяем, что имя не изменилось
        assertEquals("newemail@yandex.ru", actualUser.get().getEmail());
        assertEquals("new_login", actualUser.get().getLogin());

        userStorage.getAllUsers().clear();
    }
}
