package com.university.library.test;

import com.university.library.controller.dto.AuthorUpdateRequest;
import com.university.library.exception.AuthorNotFoundException;
import com.university.library.model.Author;
import com.university.library.repository.AuthorRepository;
import com.university.library.service.AuthorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest {
    @Mock
    private AuthorRepository authorRepository;
    @InjectMocks
    private AuthorService authorService;

    @Test
    void findAll_shouldReturnAllAuthors() {
        // ARRANGE
        Author author = new Author(UUID.randomUUID(), "fullName", 1999, "biography");
        when(authorRepository.findAll()).thenReturn(List.of(author));

        // ACT
        List<Author> result = authorService.findAll();

        // ASSERT
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getFullName()).isEqualTo("fullName");
        verify(authorRepository).findAll();
    }

    @Test
    void update_shouldThrowAuthorNotFoundException_whenAuthorNotFound() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        AuthorUpdateRequest request = new AuthorUpdateRequest("fullName", 1999, "biography");
        when(authorRepository.findById(id)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThatThrownBy(() -> authorService.update(id,request)).isInstanceOf(AuthorNotFoundException.class);
    }
}
