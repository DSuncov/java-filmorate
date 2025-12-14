package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Qualifier
@Repository
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Map<Long, User> getAllUsers() {
        String sqlQuery = """
                SELECT *
                FROM users""";
        List<User> users = jdbcTemplate.query(sqlQuery, this::rowMapper);

        return users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    @Override
    public User getUserById(Long userId) {
        String sqlQuery = """
                SELECT *
                FROM users
                WHERE users.id = ?""";

        Optional<User> optUser = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, new Object[] {userId}, this::rowMapper));

        if (optUser.isEmpty()) {
            throw new EmptyResultDataAccessException("Пользователя с id = " + userId + " отсутствует в БД", 0);
        }

        return optUser.get();
    }

    @Override
    public Map<Long, User> getFriendsByUserId(Long userId) {
        String sqlQuery = """
                SELECT *
                FROM users
                JOIN friendship ON users.id = friendship.friend_id
                WHERE friendship.user_id = ? AND friendship.status = 'CONFIRMED'""";
        List<User> users = jdbcTemplate.query(sqlQuery, this::rowMapper, userId);

        return users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    @Override
    public Map<Long, User> getCommonFriendsByUsers(Long userId, Long otherUserId) {
        String sqlQuery = """
                        SELECT u.*
                        FROM users AS u
                        INNER JOIN friendship AS f1 ON u.id = f1.friend_id
                        INNER JOIN friendship AS f2 ON u.id = f2.friend_id
                        WHERE f1.user_id = ? AND f2.user_id = ?""";
        List<User> users = jdbcTemplate.query(sqlQuery, this::rowMapper, userId, otherUserId);

        return users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    @Override
    public User createUser(User user) {
        String sqlQuery = """
                INSERT INTO users(email, login, name, birthday)
                VALUES(?, ?, ?, ?)""";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setObject(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = """
                UPDATE users
                SET email = ?, login = ?, name = ?, birthday = ?
                WHERE id = ?""";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public void addToFriends(Long userId, Long friendId) {
        String sqlQuery = """
                INSERT INTO friendship(user_id, friend_id, status)
                VALUES(?, ?, 'CONFIRMED')""";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void removeFromFriends(Long userId, Long friendId) {
        String sqlQuery = """
                DELETE FROM friendship
                WHERE user_id = ? AND friend_id = ?""";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    private User rowMapper(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setEmail(resultSet.getString("email"));
        user.setLogin(resultSet.getString("login"));
        user.setName(resultSet.getString("name"));
        user.setBirthday(resultSet.getDate("birthday").toLocalDate());
        return user;
    }
}
