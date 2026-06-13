package com.university.library.test;

import com.university.library.exception.NoCopiesAvailableException;
import com.university.library.model.Book;
import com.university.library.model.Reader;
import com.university.library.repository.BookRepository;
import com.university.library.repository.LoanRepository;
import com.university.library.repository.ReaderRepository;
import com.university.library.service.LoanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // чистый юнит тест без поднятия спринга, только мокито создает моки и реальный объект сервиса, быстрые тесты
public class LoanServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private ReaderRepository readerRepository;
    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;

    @Test
    void issueLoan_shouldThrowNoCopiesAvailableException_ifNoCopiesAvailable() {
        // ARRANGE
        UUID bookId = UUID.randomUUID();
        UUID readerId = UUID.randomUUID();
        Book book = new Book(bookId, "title", "978-5-04-116820-7", 1999, UUID.randomUUID(), 5, 0);
        Reader reader = new Reader(readerId, "name", "123@gmail.com", LocalDate.of(2025, 6, 7));

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));

        // ACT + ASSERT
        assertThatThrownBy(() -> loanService.issueLoan(bookId, readerId))
                .isInstanceOf(NoCopiesAvailableException.class);
    }

    @Test
    void issueLoan_shouldDecrementCopiesAvailable_whenSuccessful() {
        // ARRANGE
        Book book = new Book(UUID.randomUUID(), "title", "978-5-04-116820-7", 1999, UUID.randomUUID(), 5, 4);
        Reader reader = new Reader(UUID.randomUUID(), "name", "123@gmail.com", LocalDate.of(2025, 6, 7));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(readerRepository.findById(reader.getId())).thenReturn(Optional.of(reader));

        // ACT
        loanService.issueLoan(book.getId(), reader.getId());

        // ASSERT
        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).update(captor.capture());
        assertThat(captor.getValue().getCopiesAvailable()).isEqualTo(3);

    }
}
