package com.university.library.test;

import com.university.library.controller.dto.ReaderUpdateRequest;
import com.university.library.exception.ReaderNotFoundException;
import com.university.library.model.Reader;
import com.university.library.repository.ReaderRepository;
import com.university.library.service.ReaderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReaderServiceTest {
    @Mock
    private ReaderRepository readerRepository;
    @InjectMocks
    private ReaderService readerService;

    @Test
    void findAll_shouldReturnAllReaders() {
        // ARRANGE
        Reader reader = new Reader(UUID.randomUUID(), "fullName", "email@gmail.com", LocalDate.of(2020, 7, 6));
        when(readerRepository.findAll()).thenReturn(List.of(reader));

        // ACT
        List<Reader> result = readerService.findAll();

        // ASSERT
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getFullName()).isEqualTo("fullName");
        verify(readerRepository).findAll();
    }

    @Test
    void update_shouldThrowReaderNotFoundException_whenReaderNotFound() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        ReaderUpdateRequest request = new ReaderUpdateRequest("fullName", "email@gmail.com");
        when(readerRepository.findById(id)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThatThrownBy(() -> readerService.update(id, request)).isInstanceOf(ReaderNotFoundException.class);
    }
}
