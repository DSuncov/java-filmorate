package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
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
    private final DirectorDbStorage directorDbStorage;

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
        film.setDirectors(directorDbStorage.getDirectorsByFilmId(film.getId()));

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

    public List<Film> getFilmsByDirector(Long directorId) {
        String sqlQuery = """
                SELECT f.*
                FROM films f
                LEFT JOIN directors_films df ON f.id = df.director_id
                LEFT JOIN directors d ON df.film_id = d.id
                WHERE d.id = ?""";
        return jdbcTemplate.query(sqlQuery, this::rowMapper, directorId);
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

        Optional<List<Director>> optDirector = Optional.ofNullable(film.getDirectors());
        if (optDirector.isPresent()) {

            Set<Director> directorSet = new HashSet<>(optDirector.get());
            String sql = """
                    INSERT INTO directors_films(film_id, director_id)
                    VALUES(?, ?)
                    """;
            for (Director director : directorSet) {
                jdbcTemplate.update(sql, film.getId(), director.getId());
            }

            film.setDirectors(directorDbStorage.getDirectorsByFilmId(film.getId()));
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

        Optional.ofNullable(film.getMpa()).ifPresent(rating -> film.setMpa(ratingDbStorage.getRatingById(rating.getId())));

        Optional<List<Genre>> optGenres = Optional.ofNullable(film.getGenres());
        if (optGenres.isPresent() && !optGenres.get().isEmpty()) {
            List<Genre> oldGenres = genreDbStorage.getGenresByFilmId(film.getId());
            Set<Genre> newGenreSet = new HashSet<>(optGenres.get());
            String insertGenres = """
                UPDATE genre_films
                SET genre_id = ?
                WHERE film_id = ? AND genre_id = ?
                """;
            for (Genre genreNew : newGenreSet) {
                if (!(genreNew.getId() > 0 && genreNew.getId() <= genreDbStorage.getAllGenres().size())) {
                    throw new NotFoundException("Жанр с id = " + genreNew.getId() + " отсутствует в БД");
                }
                for (Genre genreOld : oldGenres) {
                    jdbcTemplate.update(insertGenres, genreNew.getId(), film.getId(), genreOld.getId());
                }
            }
            film.setGenres(genreDbStorage.getGenresByFilmId(film.getId()));
        }

        Optional<List<Genre>> result = Optional.ofNullable(film.getGenres());
        if (result.isEmpty()) {
            film.setGenres(genreDbStorage.getGenresByFilmId(film.getId()));
        }

        Optional<List<Director>> optDirector = Optional.ofNullable(film.getDirectors());
        if (optDirector.isPresent() && !optDirector.get().isEmpty()) {
            Set<Director> newDirectors = new HashSet<>(optDirector.get());
            String sql = """
                    INSERT INTO directors_films(film_id, director_id)
                    VALUES(?, ?)
                    """;
            for (Director director : newDirectors) {
                jdbcTemplate.update(sql, film.getId(), director.getId());
            }
            film.setDirectors(directorDbStorage.getDirectorsByFilmId(film.getId()));
        }
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

    @Override
    public List<Film> getAllFilmsByDirectorAndSortedBy(Long directorId, String sortRule) {
        if (sortRule == null) {
            throw new NotFoundException("Параметр для сортировки не задан.");
        }

        switch (sortRule) {
            case "year" -> {
                String sql = """
                SELECT
                    f.id,
                    f.name,
                    f.description,
                    f.releaseDate,
                    f.duration,
                    f.rating_id,
                FROM films f
                INNER JOIN directors_films df ON f.id = df.film_id
                WHERE df.director_id = ?
                ORDER BY releaseDate""";

                return loadGenresAndDirectors(sql, directorId);
            }

            case "likes" -> {
                String sql = """
                SELECT
                    f.id,
                    f.name,
                    f.description,
                    f.releaseDate,
                    f.duration,
                    f.rating_id,
                    COUNT(lf.user_id) AS likes_count
                FROM films f
                INNER JOIN directors_films df ON f.id = df.film_id
                INNER JOIN likes_films lf ON f.id = lf.film_id
                WHERE df.director_id = ?
                GROUP BY
                    f.id
                ORDER BY COUNT(lf.user_id) DESC""";

                return loadGenresAndDirectors(sql, directorId);
            }
            default -> throw new NotFoundException("Такого параметра для сортировки не существует.");
        }
    }

    private List<Film> loadGenresAndDirectors(String sql, Long directorId) {
        List<Film> films = jdbcTemplate.query(sql, this::rowMapper, directorId);
        for (Film film : films) {
            film.setGenres(genreDbStorage.getGenresByFilmId(film.getId()));
            film.setDirectors(directorDbStorage.getDirectorsByFilmId(film.getId()));
        }
        return films;
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
