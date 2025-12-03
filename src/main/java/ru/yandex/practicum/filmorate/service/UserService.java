package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.Mapper;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.validation.UserServiceValidation;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage dbUserStorage;
    private final UserServiceValidation userServiceValidation;
    private final Mapper mapper;

    public Collection<UserDTO> getAllUsers() {
        log.info("Отправляем запрос на получение списка всех пользователей ...");
        Collection<UserDTO> listOfAllUsers = dbUserStorage.getAllUsers().values()
                .stream()
                .map(mapper::userToDto)
                .toList();
        log.info("Список пользователей отправлен клиенту.");
        return listOfAllUsers;
    }

    public UserDTO getUserById(Long userId) {
        log.info("Отправляем запрос на получение информации о пользователе с id = {} ...", userId);
        userServiceValidation.userExistInStorage(userId);
        User user = dbUserStorage.getUserById(userId);
        log.info("Информация о пользователе с id {} отправлена клиенту", userId);
        return mapper.userToDto(user);
    }

    public Collection<UserDTO> getFriendsByUserId(Long userId) {
        log.info("Отправляем запрос на получение списка друзей пользователя с id = {} ...", userId);
        userServiceValidation.userExistInStorage(userId);
        Collection<UserDTO> listFriendsByUser = dbUserStorage.getFriendsByUserId(userId).values()
                .stream()
                .map(mapper::userToDto)
                .toList();
        log.info("Список друзей пользователя с id {} отправлен клиенту", userId);
        return listFriendsByUser;
    }

    public Collection<UserDTO> getCommonFriendsByUsers(Long userId, Long otherUserId) {
        log.info("Отправляем запрос на получение списка общих друзей у пользователей с id {} и {} ...", userId, otherUserId);
        userServiceValidation.userExistInStorage(userId);
        userServiceValidation.userExistInStorage(otherUserId);
        Collection<UserDTO> listCommonFriends = dbUserStorage.getCommonFriendsByUsers(userId, otherUserId).values()
                .stream()
                .map(mapper::userToDto)
                .toList();
        log.info("Информация об общих друзьях пользователей с id {} и {} отправлена клиенту", userId, otherUserId);
        return listCommonFriends;
    }

    public UserDTO create(User user) {
        log.info("Отправляем запрос на создание нового пользователя ...");
        userServiceValidation.userValidationForCreate(user);
        User newUser = dbUserStorage.createUser(user);
        log.info("Добавлен новый пользователь с id {} и логином {}", newUser.getId(), newUser.getLogin());
        return mapper.userToDto(newUser);
    }

    public UserDTO update(User user) {
        log.info("Отправляем запрос на обновление данных пользователя ...");
        userServiceValidation.userValidationForUpdate(user);
        User updateUser = dbUserStorage.updateUser(user);
        log.info("Информация о пользователе с id {} обновлена", updateUser.getId());
        return mapper.userToDto(updateUser);
    }

    public void addToFriends(Long userId, Long friendId) {
        log.info("Отправляем запрос на добавление в список друзей ...");
        userServiceValidation.userExistInStorage(userId);
        userServiceValidation.userExistInStorage(friendId);
        dbUserStorage.addToFriends(userId, friendId);
        log.info("Пользователь с id {} отправил запрос пользователю с id {} на добавление в друзья", friendId, userId);
    }

    public void deleteFromFriends(Long userId, Long friendId) {
        log.info("Отправляем запрос на удаление из списка друзей ...");
        userServiceValidation.userExistInStorage(userId);
        userServiceValidation.userExistInStorage(friendId);
        dbUserStorage.removeFromFriends(userId, friendId);
        log.info("Пользователь с id {} удален из друзей пользователю с id {}", friendId, userId);
    }
}
