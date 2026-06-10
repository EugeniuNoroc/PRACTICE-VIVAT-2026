package com.university.library.controller;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class IsbnValidator implements ConstraintValidator<ValidIsbn, String> { // валидатор работает с аннотацией ValidIsbn и проверяет значение типа String
    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext context) { // метод вызывается автоматически когда спринг встречает @ValidIsbn, возвращает true или false
        if (isbn == null || isbn.isBlank()) return false;
        String digits = isbn.replaceAll("[^0-9X]", ""); // убираем все кроме цифр и X (978-5-04-116820-7 -> 9785041168207) для дальнейших вычислений
        // isbn бывает двух форматов - 13 цифр и 10 цифр, если не подходит ни один - невалидный
        if (digits.length() == 13) return isValidIsbn13(digits);
        if (digits.length() == 10) return isValidIsbn10(digits);
        return false;
    }

    private boolean isValidIsbn13(String digits) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int d = digits.charAt(i) - '0'; // получаем числовое значение цифры, символ '9' это число 57 в ASCII, '0' это 48. 57 - 48 = 9 - получили цифру
            sum += (i % 2 == 0) ? d : d * 3; // четные позиции берем как есть, нечетные умножаем на 3 и суммируем
        }
        int check = (10 - (sum % 10)) % 10;
        return check == (digits.charAt(12) - '0'); // вычисляем контрольную (последнюю) цифру и сравниваем с последней цифрой isbn, если совпало - валидный
    }

    private boolean isValidIsbn10(String digits) {
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int d = digits.charAt(i) - '0';
            sum += d * (10 - i);
        }
        char last = digits.charAt(9);
        sum += (last == 'X') ? 10 : (last - '0');
        return sum % 11 == 0;
    }
}
