package com.university.library.demo;

import com.university.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// Этот класс существует только для демонстрации трёх видов injection
// В реальном коде используем только constructor injection
@Component
public class InjectionDemo {

    // 1. Field injection — антипаттерн, поле private, не тестируемо
    @Autowired
    private BookRepository fieldInjected;

    // 2. Setter injection
    private BookRepository setterInjected;

    @Autowired
    public void setBookRepository(BookRepository bookRepository) {
        this.setterInjected = bookRepository;
    }

    // 3. Constructor injection — правильный способ
    private final BookRepository constructorInjected;

    public InjectionDemo(BookRepository constructorInjected) {
        this.constructorInjected = constructorInjected;
    }
}