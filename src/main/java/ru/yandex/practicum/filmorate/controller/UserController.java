package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<User> getUsers() {
        Collection<User> listOfUsers = userService.getAllUsers();
        log.info("Список пользователей отправлен");
        return listOfUsers;
    }

    @GetMapping("{id}")
    public User getUserById(@PathVariable("id") Long userId) {
        User user = userService.getUserById(userId);
        log.info("Информация о пользователе с id {} отправлена", userId);
        return user;
    }

    @PostMapping
    public User createUser(@RequestBody @Valid User user) {
        User newUser = userService.create(user);
        log.info("Добавлен новый пользователь с id {} и логином {}", user.getId(), user.getLogin());
        return newUser;
    }

    @PutMapping("{id}")
    public User updateUser(@PathVariable("id") @Min(1) Long userId, @Valid @RequestBody User user) {
        User updateUser = userService.update(userId, user);
        log.info("Информация о пользователе с id {} обновлена", userId);
        return updateUser;
    }
}
