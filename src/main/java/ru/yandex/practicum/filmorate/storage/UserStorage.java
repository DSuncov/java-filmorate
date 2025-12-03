package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

@Component
public interface UserStorage {

    Map<Long, User> getAllUsers();

    User getUserById(Long id);

    Map<Long, User> getFriendsByUserId(Long userId);

    Map<Long, User> getCommonFriendsByUsers(Long userId, Long otherUserId);

    User createUser(User user);

    User updateUser(User user);

    void addToFriends(Long userId, Long friendId);

    void removeFromFriends(Long userId, Long friendId);
}
