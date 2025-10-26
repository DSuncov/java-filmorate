package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

@Service
@Validated
public class UserService {
    private final Map<Long, User> users = new HashMap<>(); // храним всех пользователей

    public Map<Long, User> getMap() {
        return users;
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User getUserById(Long id) {
        Optional<User> optionalUser = Optional.ofNullable(users.get(id));
        return optionalUser.orElseThrow(() -> new ValidationException("Пользвателя с id = " + id + " не существуюет"));
    }

    public User create(User user) {
        Optional<String> findEmail = findEmail(user.getEmail());
        if (findEmail.isPresent()) {
            throw new ValidationException("E-mail = " + user.getEmail() + " используется другим пользователем");
        }

        if (Pattern.compile(" ").matcher(user.getLogin()).find()) {
            throw new ValidationException("Логин не может содержать пробелы");
        }

        Optional<String> findLogin = findLogin(user.getLogin());
        if (findLogin.isPresent()) {
            throw new ValidationException("Логин = " + user.getLogin() + " используется другим пользователем");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(getUserId());
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        User updateUser;
        if (user != null && user.getId() != null) {
            updateUser = update(user.getId(), user);
        } else {
            throw new ValidationException("Для обновления фильма задайте id");
        }
        return updateUser;
    }

    public User update(Long id, User user) {
        User oldUserData = getUserById(id);

        if (user == null) {
            return oldUserData;
        }

        String email = user.getEmail();
        if (email != null && !email.equals(oldUserData.getEmail())) {
            if (findEmail(email).isPresent()) {
                throw new ValidationException("E-mail = " + email + " занят");
            }
            oldUserData.setEmail(email);
        }

        String login = user.getLogin();
        if (login != null && !login.equals(oldUserData.getLogin())) {
            if (Pattern.compile(" ").matcher(user.getLogin()).find()) {
                throw new ValidationException("Логин не может содержать пробелы");
            }
            if (findLogin(login).isPresent()) {
                throw new ValidationException("Логин = " + login + " занят");
            }
            oldUserData.setLogin(login);
        }

        String name = user.getName();
        if (name != null && !name.isBlank()) {
            oldUserData.setName(name);
        }

        LocalDate birthday = user.getBirthday();
        if (birthday != null && birthday.isBefore(LocalDate.now())) {
            oldUserData.setBirthday(birthday);
        }

        return oldUserData;
    }

    private long getUserId() {
        long currentMaxId = users.values().stream()
                .map(User::getId)
                .max(Long::compare)
                .orElse(0L);
        return ++currentMaxId;
    }

    private Optional<String> findEmail(String email) {
        return users.values().stream()
                .map(User::getEmail)
                .filter(u -> u.equals(email))
                .findFirst();
    }

    private Optional<String> findLogin(String login) {
        return users.values().stream()
                .map(User::getLogin)
                .filter(u -> u.equals(login))
                .findFirst();
    }
}
