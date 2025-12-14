package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Map<Long, Genre> getAllGenres() {
        String sqlQuery = """
                SELECT *
                FROM genre""";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, this::rowMapper);

        return genres.stream()
                .collect(Collectors.toMap(Genre::getId, Function.identity()));
    }

    @Override
    public Genre getGenreById(Long genreId) {
        String sqlQuery = """
                SELECT *
                FROM genre
                WHERE id = ?""";
        return jdbcTemplate.queryForObject(sqlQuery, new Object[] {genreId}, this::rowMapper);
    }

    @Override
    public List<Genre> getGenresByFilmId(Long filmId) {
        String sqlQuery = """
                SELECT g.*
                FROM genre AS g
                LEFT JOIN genre_films AS gf ON g.id = gf.genre_id
                LEFT JOIN films AS f ON gf.film_id = f.id
                WHERE f.id = ?""";
        return jdbcTemplate.query(sqlQuery, this::rowMapper, filmId);
    }

    private Genre rowMapper(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getLong("id"));
        genre.setName(resultSet.getString("name"));
        return genre;
    }
}
