package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Map<Long, Film> getAllFilms() {
        return films;
    }

    @Override
    public Film getFilmById(Long filmId) {
        return films.get(filmId);
    }

    @Override
    public List<Film> getTopFilmsByLikes(Long count) {
        return getTopFilms(count);
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(getFilmId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Film oldFilmData = films.get(film.getId());
        oldFilmData.setName(film.getName());
        oldFilmData.setDescription(film.getDescription());
        oldFilmData.setReleaseDate(film.getReleaseDate());
        oldFilmData.setDuration(film.getDuration());
        return oldFilmData;
    }

    @Override
    public void addLikeToFilm(Long filmId, Long userId) {
        films.get(filmId).getUsersIdWhoLikes().add(userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        films.get(filmId).getUsersIdWhoLikes().remove(userId);
    }

    private long getFilmId() {
        long currentMaxId = films.values().stream()
                .map(Film::getId)
                .max(Long::compare)
                .orElse(0L);
        return ++currentMaxId;
    }

    private List<Film> getTopFilms(Long count) {

        Map<Long, Integer> filmsLikesQuantity = films.values().stream()
                .collect(Collectors.toMap(Film::getId, film -> film.getUsersIdWhoLikes().size()));


        Map<Long, Integer> sortedFilmsLikesQuantity = filmsLikesQuantity.entrySet().stream()
                .sorted(Comparator.comparingInt(v -> -v.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> {
                            throw new AssertionError();
                            },
                        LinkedHashMap::new
                ));

        return sortedFilmsLikesQuantity.keySet().stream()
                .map(films::get)
                .limit(count)
                .collect(Collectors.toList());
    }
}
