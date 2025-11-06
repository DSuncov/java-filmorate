package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>(); // храним всех пользователей

    @Override
    public Map<Long, User> getAllUsers() {
        return users;
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public Map<Long, User> getFriendsByUserId(Long userId) {
        Set<Long> friendsId = users.get(userId).getFriends();
        Map<Long, User> friends = new HashMap<>();
        for (Long friendId : friendsId) {
            User friend = users.get(friendId);
            friends.put(friendId, friend);
        }
        return friends;
    }

    @Override
    public Map<Long, User> getCommonFriendsByUsers(Long userId, Long otherUserId) {
        Set<Long> friendsOfUser = users.get(userId).getFriends();
        Set<Long> friendsOfOtherUser = users.get(otherUserId).getFriends();
        Set<Long> commonFriendsId = new TreeSet<>(friendsOfUser);
        commonFriendsId.retainAll(friendsOfOtherUser);

        Map<Long, User> commonFriends = new HashMap<>();
        for (Long friendId : commonFriendsId) {
            User friend = users.get(friendId);
            commonFriends.put(friendId, friend);
        }
        return commonFriends;
    }

    @Override
    public User createUser(User user) {
        user.setId(getUserId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        User oldUserData = users.get(user.getId());
        if (!user.getEmail().equals(oldUserData.getEmail())) {
            oldUserData.setEmail(user.getEmail());
        }
        if (!user.getLogin().equals(oldUserData.getEmail())) {
            oldUserData.setLogin(user.getLogin());
        }
        if (!(user.getName() == null || user.getName().isBlank())) {
            oldUserData.setName(user.getName());
        }
        oldUserData.setBirthday(user.getBirthday());
        return oldUserData;
    }

    @Override
    public void addToFriends(Long userId, Long friendId) {
        users.get(userId).getFriends().add(friendId);
        users.get(friendId).getFriends().add(userId);
    }

    @Override
    public void deleteUserFriend(Long userId, Long friendId) {
        users.get(userId).getFriends().remove(friendId);
        users.get(friendId).getFriends().remove(userId);
    }

    private long getUserId() {
        long currentMaxId = users.values().stream()
                .map(User::getId)
                .max(Long::compare)
                .orElse(0L);
        return ++currentMaxId;
    }
}
