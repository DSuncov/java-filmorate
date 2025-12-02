package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RatingDbStorage implements RatingStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Map<Long, Rating> getAllRatings() {
        String sqlQuery = """
                SELECT *
                FROM rating""";
        List<Rating> genres = jdbcTemplate.query(sqlQuery, this::rowMapper);

        return genres.stream()
                .collect(Collectors.toMap(Rating::getId, Function.identity()));
    }

    @Override
    public Rating getRatingById(Long ratingId) {
        String sqlQuery = """
                SELECT *
                FROM rating
                WHERE id = ?""";
        return jdbcTemplate.queryForObject(sqlQuery, new Object[] {ratingId}, this::rowMapper);
    }

    private Rating rowMapper(ResultSet resultSet, int rowNum) throws SQLException {
        Rating rating = new Rating();
        rating.setId(resultSet.getLong("id"));
        rating.setName(resultSet.getString("name"));
        return rating;
    }
}
