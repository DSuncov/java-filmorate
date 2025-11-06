package ru.yandex.practicum.filmorate.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotWhiteSpaceValidator.class)
public @interface NotWhiteSpace {
    String message() default "Логин не может содержать пробелы";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String login() default "login";
}
