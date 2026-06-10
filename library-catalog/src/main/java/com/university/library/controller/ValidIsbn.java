package com.university.library.controller;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD}) // только на полях класса
@Retention(RetentionPolicy.RUNTIME) // должна быть доступна во время выполнения программы
@Constraint(validatedBy  = IsbnValidator.class) // говорим спринг какой класс содержит логику проверки
public @interface ValidIsbn {
    // три метода обязательны для любого constraint по Bean Validation: текст ошибки по умолчанию, groups и payload
    String message() default "Невалидный ISBN";
    Class<?>[] groups() default {};                  // пустые массивы для продвинутых сценариев
    Class<? extends Payload>[] payload() default {}; // пока пустые
}
