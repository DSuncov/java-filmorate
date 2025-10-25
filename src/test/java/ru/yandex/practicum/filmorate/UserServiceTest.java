package ru.yandex.practicum.filmorate;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    final UserService userService = new UserService();
    final UserController userController = new UserController(userService);

    final List<User> users = List.of(
            new User("ya.user1@yandex.ru", "User1", null, null),
            new User("ya.user2@yandex.ru", "User2", null, null),
            new User("ya.user3@yandex.ru", "User3", null, null),
            new User("ya.user4@yandex.ru", "User4", null, null),
            new User("ya.user5@yandex.ru", "User5", null, null),
            new User("ya.user6@yandex.ru", "User6", null, null),
            new User("ya.user7@yandex.ru", "User7", null, null),
            new User("ya.user8@yandex.ru", "User8", null, null),
            new User("ya.user9@yandex.ru", "User9", null, null),
            new User("ya.user10@yandex.ru", "User10", null, null));

    @BeforeEach
    public void createUsersForTesting() {
        for (User user : users) {
            userController.createUser(user);
        }
    }

    @AfterEach
    public void clear() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        userService.getMap().clear();
    }

    @DisplayName("Проверяет размер коллекции, которую возвращает метод getAllUsers")
    @Test
    void userService_Return_Correct_Numbers_Of_Users() {
        int expectedSize = 10;
        assertEquals(expectedSize, userController.getUsers().size());
    }

    @DisplayName("Возвращает пользоваля по id")
    @Test
    void userService_Return_Correct_User_By_Id() {
        User expectedUserById1 = users.getFirst();
        User expectedUserById5 = users.get(4);
        User expectedUserById10 = users.get(9);

        assertEquals(expectedUserById1, userController.getUserById(1L));
        assertEquals(expectedUserById5, userController.getUserById(5L));
        assertEquals(expectedUserById10, userController.getUserById(10L));
        assertThrows(ValidationException.class, () -> userController.getUserById(11L));
        assertThrows(ValidationException.class, () -> userController.getUserById(16L));
        assertThrows(ValidationException.class, () -> userController.getUserById(0L));
    }

    @DisplayName("Проверяет метод create()")
    @Test
    void userService_Create_User() {
        assertNotNull(userController.getUserById(1L));
        assertNotNull(userController.getUserById(5L));
        assertNotNull(userController.getUserById(10L));
        assertThrows(ValidationException.class, () -> userController.getUserById(12L));
    }

    @DisplayName("Проверяет метод create(). Должен возвращать исключение, если email занят")
    @Test
    void userService_Create_User_with_Duplicate_Email() {
        User userWithDuplicateEmail = new User("ya.user8@yandex.ru", "User13", null, null);
        assertThrows(ValidationException.class, () -> userController.createUser(userWithDuplicateEmail));
    }

    @DisplayName("Проверяет метод update()")
    @Test
    void userService_Update_Email() {
        String newEmail = "ya.user4new@yandex.ru";
        String name = users.get(3).getName();
        userController.updateUser(4L, new User(newEmail, null, null, null));

        assertEquals(newEmail, userController.getUserById(4L).getEmail());
        assertEquals(name, userController.getUserById(4L).getLogin()); // Т.к. при создании объекта не было задано имя, то оно должен совпадать с логином
    }

    @DisplayName("Проверяет метод update()")
    @Test
    void userService_Update_Login() {
        String newLogin = "User6new";
        userController.updateUser(6L, new User(null, newLogin, null, null));

        assertEquals(newLogin, userController.getUserById(6L).getLogin());
        assertNotNull(userController.getUserById(6L).getEmail()); // Проверяем, что email не стал null
        assertEquals("ya.user6@yandex.ru", userController.getUserById(6L).getEmail()); // Проверяем, что email не изменился
    }

    @DisplayName("Проверяет метод update()")
    @Test
    void userService_Update_Name() {
        String newName = "User9new";
        userController.updateUser(9L, new User(null, null, newName, null));
        assertEquals(newName, userController.getUserById(9L).getName());
        assertNotNull(userController.getUserById(9L).getEmail()); // Проверяем, что email не стал null
        assertNotNull(userController.getUserById(9L).getLogin()); // Проверяем, что login не стал null
    }
}
