package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public Collection<User> getUsers() {
        log.info("Отправляем запрос на получение списка всех пользователей ...");
        Collection<User> listOfUsers = userService.getAllUsers();
        log.info("Список пользователей отправлен клиенту.");
        return listOfUsers;
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable("id") @NotNull(message = "id пользователя должно быть задано") Long userId) {
        log.info("Отправляем запрос на получение информации о пользователе с id = {} ...", userId);
        User user = userService.getUserById(userId);
        log.info("Информация о пользователе с id {} отправлена клиенту", userId);
        return user;
    }

    @GetMapping("/users/{id}/friends")
    public Collection<User> getFriendsByUserId(@PathVariable("id") @NotNull(message = "id пользователя должно быть задано") Long userId) {
        log.info("Отправляем запрос на получение списка друзей пользователя с id = {} ...", userId);
        Collection<User> listOfFriendsBySpecifiedUser = userService.getFriendsByUserId(userId);
        log.info("Список друзей пользователя с id {} отправлен клиенту", userId);
        return listOfFriendsBySpecifiedUser;
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriendsByUsers(
            @PathVariable("id") @NotNull(message = "id пользователя должно быть задано") Long userId,
            @PathVariable("otherId") @NotNull(message = "id другого должно быть задано") Long otherUserId
    ) {
        log.info("Отправляем запрос на получение списка общих друзей у пользователей с id {} и {} ...", userId, otherUserId);
        Collection<User> listOfCommonFriends = userService.getCommonFriendsByUsers(userId, otherUserId);
        log.info("Информация об общих друзьях пользователей с id {} и {} отправлена клиенту", userId, otherUserId);
        return listOfCommonFriends;
    }

    @PostMapping("/users")
    public User createUser(@NotNull(message = "Передано пустое значение User") @Valid @RequestBody User user) {
        log.info("Отправляем запрос на создание нового пользователя ...");
        User newUser = userService.create(user);
        log.info("Добавлен новый пользователь с id {} и логином {}", user.getId(), user.getLogin());
        return newUser;
    }

    @PutMapping("/users")
    public User updateUser(@NotNull(message = "Передано пустое значение User") @Valid @RequestBody User user) {
        log.info("Отправляем запрос на обновление данных пользователя ...");
        User updateUser = userService.update(user);
        log.info("Информация о пользователе с id {} обновлена", user.getId());
        return updateUser;
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addToFriends(@PathVariable("id") Long userId, @PathVariable("friendId") Long friendId) {
        log.info("Отпрвляем запрос на добавление в список друзей ...");
        userService.addToFriends(userId, friendId);
        log.info("Пользователь с id {} добавлен в друзья пользователю с id {}", friendId, userId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable("id") Long userId, @PathVariable("friendId") Long friendId) {
        log.info("Отправляем запрос на удаление из списка друзей ...");
        userService.deleteFromFriends(userId, friendId);
        log.info("Пользователь с id {} удален из друзей пользователю с id {}", friendId, userId);
    }
}
