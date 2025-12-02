package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.validation.UserServiceValidation;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage inMemoryUserStorage;
    private final UserServiceValidation userServiceValidation;

    public Collection<User> getAllUsers() {
        log.info("Отправляем запрос на получение списка всех пользователей ...");
        Collection<User> listOfAllUsers = inMemoryUserStorage.getAllUsers().values();
        log.info("Список пользователей отправлен клиенту.");
        return listOfAllUsers;
    }

    public User getUserById(Long userId) {
        log.info("Отправляем запрос на получение информации о пользователе с id = {} ...", userId);
        userServiceValidation.userExistInStorage(userId);
        User user = inMemoryUserStorage.getUserById(userId);
        log.info("Информация о пользователе с id {} отправлена клиенту", userId);
        return user;
    }

    public Collection<User> getFriendsByUserId(Long userId) {
        log.info("Отправляем запрос на получение списка друзей пользователя с id = {} ...", userId);
        userServiceValidation.userExistInStorage(userId);
        Collection<User> listFriendsByUser = inMemoryUserStorage.getFriendsByUserId(userId).values();
        log.info("Список друзей пользователя с id {} отправлен клиенту", userId);
        return listFriendsByUser;
    }

    public Collection<User> getCommonFriendsByUsers(Long userId, Long otherUserId) {
        log.info("Отправляем запрос на получение списка общих друзей у пользователей с id {} и {} ...", userId, otherUserId);
        userServiceValidation.userExistInStorage(userId);
        userServiceValidation.userExistInStorage(otherUserId);
        Collection<User> listCommonFriends = inMemoryUserStorage.getCommonFriendsByUsers(userId, otherUserId).values();
        log.info("Информация об общих друзьях пользователей с id {} и {} отправлена клиенту", userId, otherUserId);
        return listCommonFriends;
    }

    public User create(User user) {
        log.info("Отправляем запрос на создание нового пользователя ...");
        userServiceValidation.userValidationForCreate(user);
        User newUser = inMemoryUserStorage.createUser(user);
        log.info("Добавлен новый пользователь с id {} и логином {}", newUser.getId(), newUser.getLogin());
        return newUser;
    }

    public User update(User user) {
        log.info("Отправляем запрос на обновление данных пользователя ...");
        userServiceValidation.userValidationForUpdate(user);
        User updateUser = inMemoryUserStorage.updateUser(user);
        log.info("Информация о пользователе с id {} обновлена", updateUser.getId());
        return updateUser;
    }

    public void addToFriends(Long userId, Long friendId) {
        log.info("Отпрвляем запрос на добавление в список друзей ...");
        userServiceValidation.userExistInStorage(userId);
        userServiceValidation.userExistInStorage(friendId);
        inMemoryUserStorage.addToFriends(userId, friendId);
        log.info("Пользователь с id {} добавлен в друзья пользователю с id {}", friendId, userId);

    }

    public void deleteFromFriends(Long userId, Long friendId) {
        log.info("Отправляем запрос на удаление из списка друзей ...");
        userServiceValidation.userExistInStorage(userId);
        userServiceValidation.userExistInStorage(friendId);
        inMemoryUserStorage.deleteUserFriend(userId, friendId);
        log.info("Пользователь с id {} удален из друзей пользователю с id {}", friendId, userId);
    }
}
