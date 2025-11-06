package ru.yandex.practicum.filmorate.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class NotWhiteSpaceValidator implements ConstraintValidator<NotWhiteSpace, String> {
    @Override
    public boolean isValid(String login, ConstraintValidatorContext constraintValidatorContext) {
        return !Pattern.compile(" ").matcher(login).find();
    }
}
