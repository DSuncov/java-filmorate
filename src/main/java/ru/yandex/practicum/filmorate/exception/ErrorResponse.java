package ru.yandex.practicum.filmorate.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class ErrorResponse {
    private final Integer error;
    private final String description;
}
