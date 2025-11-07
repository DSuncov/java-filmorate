package ru.yandex.practicum.filmorate.service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserServiceValidation {

    private final UserStorage inMemoryUserStorage;

    /*
    В контроллере с помощью аннотации проверили , что User не null и что поля валидны.
    Остается проверить, что пользователя с таким email и логином нет в базе
     */
    public void userValidationForCreate(User user) {
        emailValidation(user);
        loginValidation(user);
    }

    /*
    В контроллере с помощью аннотации проверили , что User не null и что поля валидны.
    Остается проверить, что пользователь с переданным в запросе id есть в базе.
    Далее проверяем, что новые email и login не используются другими пользователями.
     */
    public void userValidationForUpdate(User user) {
        userExistInStorage(user.getId());
        emailValidation(user);
        loginValidation(user);
    }

    public void userExistInStorage(Long userId) {
        if (!inMemoryUserStorage.getAllUsers().containsKey(userId)) {
            throw new NotFoundException("Фильма с id = " + userId + " не существует.");
        }
    }

    private void emailValidation(User user) {
        Optional<String> findEmail = inMemoryUserStorage.getAllUsers().values()
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
        Optional<String> findLogin = inMemoryUserStorage.getAllUsers().values()
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
