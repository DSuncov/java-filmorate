package ru.yandex.practicum.filmorate.service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserServiceValidation {

    private final UserStorage dbUserStorage;

    public void userValidationForCreate(User user) {
        emailValidation(user);
        loginValidation(user);
    }

    public void userValidationForUpdate(User user) {
        userExistInStorage(user.getId());
        emailValidation(user);
        loginValidation(user);
    }

    public void userExistInStorage(Long userId) {

        if (Optional.ofNullable(dbUserStorage.getUserById(userId)).isEmpty()) {
            throw new EmptyResultDataAccessException("Пользователь с id = " + " отсутствует в БД", 0);
        }
    }

    private void emailValidation(User user) {
        Optional<String> findEmail = dbUserStorage.getAllUsers().values()
                .stream()
                .filter(u -> !u.getId().equals(user.getId()))
                .map(User::getEmail)
                .filter(u -> u.equals(user.getEmail()))
                .findFirst();

        if (findEmail.isPresent()) {
            throw new DuplicatedDataException("E-mail = " + user.getEmail() + " используется другим пользователем");
        }
    }

    private void loginValidation(User user) {
        Optional<String> findLogin = dbUserStorage.getAllUsers().values()
                .stream()
                .filter(u -> !u.getId().equals(user.getId()))
                .map(User::getLogin)
                .filter(u -> u.equals(user.getLogin()))
                .findFirst();

        if (findLogin.isPresent()) {
            throw new DuplicatedDataException("Логин = " + user.getLogin() + " используется другим пользователем");
        }
    }
}
