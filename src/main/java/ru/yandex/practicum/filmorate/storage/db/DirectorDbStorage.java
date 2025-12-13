package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Map<Long, Director> getAllDirectors() {
        String sqlQuery = """
                SELECT *
                FROM directors""";

        List<Director> directors = jdbcTemplate.query(sqlQuery, this::rowMapper);

        return directors.stream()
                .collect(Collectors.toMap(Director::getId, Function.identity()));
    }

    @Override
    public Director getDirectorById(Long directorId) {
        String sqlQuery = """
                SELECT *
                FROM directors
                WHERE id = ?""";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, new Object[] {directorId}, this::rowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Режиссер с id = " + directorId + " отсутствует в БД");
        }
    }

    @Override
    public List<Director> getDirectorsByFilmId(Long filmId) {
        String sqlQuery = """
                SELECT d.*
                FROM directors d
                LEFT JOIN directors_films df ON d.id = df.director_id
                LEFT JOIN films f ON df.film_id = f.id
                WHERE f.id = ?""";
        return jdbcTemplate.query(sqlQuery, this::rowMapper, filmId);
    }

    @Override
    public Director createDirector(Director director) {
        String sqlQuery = """
                INSERT INTO directors(name)
                VALUES(?)""";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {

        try {
            getDirectorById(director.getId());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Режиссер с id = " + director.getId() + " отсутствует в БД");
        }

        String sqlQuery = """
                UPDATE directors
                SET name = ?
                WHERE id = ?""";

        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(Long directorId) {
        String sqlQuery = """
                DELETE FROM directors
                WHERE id = ?""";

        jdbcTemplate.update(sqlQuery, directorId);
    }

    private Director rowMapper(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
