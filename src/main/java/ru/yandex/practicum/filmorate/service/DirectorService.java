package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.DirectorDTO;
import ru.yandex.practicum.filmorate.dto.Mapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.db.DirectorDbStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorService {

    private final DirectorDbStorage directorDbStorage;
    private final Mapper mapper;

    public Collection<DirectorDTO> getAllDirectors() {
        log.info("Отправляем запрос на получние списка всех режиссеров ...");
        Collection<DirectorDTO> listOfAllDirectors = directorDbStorage.getAllDirectors().values()
                .stream()
                .map(mapper::directorToDTO)
                .toList();
        log.info("Список всех режиссеров отправлен клиенту.");
        return listOfAllDirectors;
    }

    public DirectorDTO getDirectorById(Long directorId) {
        log.info("Отправляем запрос на получение информации о режиссере с id = {} ...", directorId);
        Director director = directorDbStorage.getDirectorById(directorId);
        log.info("Информация о режиссере с id = {} отправлена клиенту.", directorId);
        return mapper.directorToDTO(director);
    }


    public DirectorDTO createDirector(Director director) {
        log.info("Отправляем запрос на создание в БД записи о режиссере ...");
        Director newDirector = directorDbStorage.createDirector(director);
        log.info("Режиссер добавлен в БД.");
        return mapper.directorToDTO(newDirector);
    }

    public DirectorDTO updateDirector(Director director) {
        log.info("Отправляем запрос на обновление данных о режиссере ...");
        Director updatedDirector = directorDbStorage.updateDirector(director);
        log.info("Информация о фильме с id = {} обновлена", director.getId());
        return mapper.directorToDTO(updatedDirector);
    }

    public void deleteDirector(Long directorId) {
        log.info("Отправляем запрос на удаление режиссера с id {}", directorId);
        directorDbStorage.deleteDirector(directorId);
        log.info("Запись о режиссере с id = {} удалена из БД.", directorId);
    }
}
