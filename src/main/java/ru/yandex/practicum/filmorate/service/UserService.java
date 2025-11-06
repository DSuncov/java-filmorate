package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.validation.UserServiceValidation;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage inMemoryUserStorage;
    private final UserServiceValidation userServiceValidation;

    public Collection<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers().values();
    }

    public User getUserById(Long id) {
        userServiceValidation.userExistInStorage(id);
        return inMemoryUserStorage.getUserById(id);
    }

    public Collection<User> getFriendsByUserId(Long userId) {
        userServiceValidation.userExistInStorage(userId);
        return inMemoryUserStorage.getFriendsByUserId(userId).values();
    }

    public Collection<User> getCommonFriendsByUsers(Long userId, Long otherUserId) {
        userServiceValidation.userExistInStorage(userId);
        userServiceValidation.userExistInStorage(otherUserId);
        return inMemoryUserStorage.getCommonFriendsByUsers(userId, otherUserId).values();
    }

    public User create(User user) {
        userServiceValidation.userValidationForCreate(user);
        //Если проверки пройдены успешно - создаем пользователя
        return inMemoryUserStorage.createUser(user);
    }

    public User update(User user) {
        userServiceValidation.userValidationForUpdate(user);
        //Если проверки пройдены успешно - создаем пользователя
        return inMemoryUserStorage.updateUser(user);
    }

    public void addToFriends(Long userId, Long friendId) {
        userServiceValidation.userExistInStorage(userId);
        userServiceValidation.userExistInStorage(friendId);
        inMemoryUserStorage.addToFriends(userId, friendId);
    }

    public void deleteFromFriends(Long userId, Long friendId) {
        userServiceValidation.userExistInStorage(userId);
        userServiceValidation.userExistInStorage(friendId);
        inMemoryUserStorage.deleteUserFriend(userId, friendId);
    }
}
