package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.Mapper;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final Mapper mapper;

    @GetMapping("/users")
    public ResponseEntity<Collection<UserDTO>> getUsers() {
        var listOfAllUsers = userService.getAllUsers()
                .stream()
                .map(mapper::userToDto)
                .toList();
        return ResponseEntity.ok(listOfAllUsers);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") @NotNull(message = "id пользователя должно быть задано") Long userId) {
        var user = userService.getUserById(userId);
        return ResponseEntity.ok(mapper.userToDto(user));
    }

    @GetMapping("/users/{id}/friends")
    public ResponseEntity<Collection<UserDTO>> getFriendsByUserId(@PathVariable("id") @NotNull(message = "id пользователя должно быть задано") Long userId) {
        var listOfFriendsByUser = userService.getFriendsByUserId(userId)
                .stream()
                .map(mapper::userToDto)
                .toList();
        return ResponseEntity.ok(listOfFriendsByUser);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public ResponseEntity<Collection<UserDTO>> getCommonFriendsByUsers(
            @PathVariable("id") @NotNull(message = "id пользователя должно быть задано") Long userId,
            @PathVariable("otherId") @NotNull(message = "id другого должно быть задано") Long otherUserId
    ) {
        var listOfCommonFriends = userService.getCommonFriendsByUsers(userId, otherUserId)
                .stream()
                .map(mapper::userToDto)
                .toList();
        return ResponseEntity.ok(listOfCommonFriends);
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@NotNull(message = "Передано пустое значение User") @Valid @RequestBody User user) {
        var newUser = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.userToDto(newUser));
    }

    @PutMapping("/users")
    public ResponseEntity<UserDTO> updateUser(@NotNull(message = "Передано пустое значение User") @Valid @RequestBody User user) {
        var updateUser = userService.update(user);
        return ResponseEntity.ok(mapper.userToDto(updateUser));
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addToFriends(@PathVariable("id") Long userId, @PathVariable("friendId") Long friendId) {
        userService.addToFriends(userId, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable("id") Long userId, @PathVariable("friendId") Long friendId) {
        userService.deleteFromFriends(userId, friendId);
    }
}
