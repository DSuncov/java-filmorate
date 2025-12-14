package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Qualifier
@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final RatingDbStorage ratingDbStorage;
    private final GenreDbStorage genreDbStorage;

    @Override
    public Map<Long, Film> getAllFilms() {
        String sqlQuery = """
                SELECT *
                FROM films""";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::rowMapper);

        return films.stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        String sqlQuery = """
                SELECT *
                FROM films
                WHERE id = ?""";

        List<Film> result = jdbcTemplate.query(sqlQuery, new Object[]{filmId}, this::rowMapper);

        if (result.isEmpty()) {
            return Optional.empty();
        }

        Film film = result.getFirst();
        film.setGenres(genreDbStorage.getGenresByFilmId(film.getId()));

        return Optional.of(film);
    }

    @Override
    public List<Film> getTopFilmsByLikes(Long count) {
        String sqlQuery = """
                SELECT f.*, COUNT(lf.user_id) AS likes_count
                FROM films AS f
                LEFT JOIN likes_films AS lf ON f.id = lf.film_id
                GROUP BY f.id
                ORDER BY likes_count DESC
                LIMIT ?
                """;
        return jdbcTemplate.query(sqlQuery, this::rowMapper, count);
    }

    @Override
    public Film createFilm(Film film) {

        Optional.ofNullable(film.getMpa()).ifPresent(rating -> film.setMpa(ratingDbStorage.getRatingById(rating.getId())));

        String sqlQuery = """
                INSERT INTO films(name, description, releaseDate, duration, rating_id)
                VALUES(?, ?, ?, ?, ?)""";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        Optional<List<Genre>> optGenres = Optional.ofNullable(film.getGenres());
        if (optGenres.isPresent()) {
            Set<Genre> genreSet = new HashSet<>(optGenres.get());
            String insertGenres = """
                INSERT INTO genre_films(film_id, genre_id)
                VALUES(?, ?)
                """;
            for (Genre genre : genreSet) {
                if (!(genre.getId() > 0 && genre.getId() <= genreDbStorage.getAllGenres().size())) {
                    throw new NotFoundException("Жанр с id = " + genre.getId() + " отсутствует в БД");
                }
                jdbcTemplate.update(insertGenres, film.getId(), genre.getId());
            }

            film.setGenres(genreDbStorage.getGenresByFilmId(film.getId()));
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (getFilmById(film.getId()).isEmpty()) {
            throw new NotFoundException("Фильм с id = " + film.getId() + " отсутсвует в БД");
        }

        String sqlQuery = """
                UPDATE films
                SET name = ?, description = ?, releaseDate = ?, duration = ?, rating_id = ?
                WHERE id = ?""";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        return film;
    }

    @Override
    public void addLikeToFilm(Long filmId, Long userId) {
        String sqlQuery = """
                INSERT INTO likes_films(film_id, user_id)
                VALUES(?, ?)""";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sqlQuery = """
                DELETE FROM likes_films
                WHERE film_id = ? AND user_id = ?""";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    private Film rowMapper(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("releaseDate").toLocalDate());
        film.setDuration(resultSet.getLong("duration"));

        int mpaId = resultSet.getInt("rating_id");
        if (mpaId > 0 && mpaId <= ratingDbStorage.getAllRatings().size()) {
            film.setMpa(ratingDbStorage.getRatingById((long) mpaId));
        }
        return film;
    }
}
