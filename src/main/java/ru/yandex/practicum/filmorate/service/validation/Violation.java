package ru.yandex.practicum.filmorate.service.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Violation {
    private final String unvalidatedFieldName;
    private final String message;
}
