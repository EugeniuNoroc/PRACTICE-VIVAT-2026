package com.university.library.test;

import com.university.library.controller.dto.BookUpdateRequest;
import com.university.library.exception.BookNotFoundException;
import com.university.library.model.Book;
import com.university.library.repository.BookRepository;
import com.university.library.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @InjectMocks
    private BookService bookService;

    @Test
    void findAll_shouldReturnAllBooks() {
        // ARRANGE
        Book book = new Book(UUID.randomUUID(), "title", "978-5-04-116820-7", 1999, UUID.randomUUID(), 5, 3);
        when(bookRepository.findAll()).thenReturn(List.of(book)); // когда кто-то вызовет findAll() на моке - вернуть вот этот список

        // ACT
        List<Book> result =  bookService.findAll();

        // ASSERT
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo("title");
        verify(bookRepository).findAll();
    }

    @Test
    void update_shouldThrowBookNotFoundException_whenBookNotFound() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        BookUpdateRequest request = new BookUpdateRequest("new title", 5);
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThatThrownBy(() ->  bookService.update(id, request)).isInstanceOf(BookNotFoundException.class);
    }
}
