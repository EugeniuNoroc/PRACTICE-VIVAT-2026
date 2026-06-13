package com.university.library.test;

import com.university.library.controller.AuthorController;
import com.university.library.controller.AuthorMapper;
import com.university.library.controller.dto.AuthorCreateRequest;
import com.university.library.controller.dto.AuthorResponse;
import com.university.library.model.Author;
import com.university.library.service.AuthorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@WebMvcTest(AuthorController.class)
@AutoConfigureRestTestClient
public class AuthorControllerTest {
    @Autowired
    RestTestClient client;

    @MockitoBean
    AuthorService authorService;

    @MockitoBean
    AuthorMapper authorMapper;

    @Test
    void findById_shouldReturn200_whenAuthorExists() {
        UUID id = UUID.randomUUID();
        Author author = new Author(id, "fullName", 1999, "biography");
        AuthorResponse response = new AuthorResponse(id, "fullName", 1999, "biography");

        when(authorService.findById(id)).thenReturn(Optional.of(author));
        when(authorMapper.toResponse(author)).thenReturn(response);

        client.get()
                .uri("/api/authors/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorResponse.class)
                .value(authorResponse -> assertThat(authorResponse.fullName()).isEqualTo("fullName"));
    }

    @Test
    void findById_shouldReturn404_whenAuthorDoesNotFound() {
        UUID id = UUID.randomUUID();
        when(authorService.findById(id)).thenReturn(Optional.empty());

        client.get()
                .uri("/api/authors/{id}", id)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void save_shouldReturn400_whenNamaIsBlank() {
        AuthorCreateRequest authorCreateRequest = new AuthorCreateRequest(
                "",
                1999,
                "biography"
        );

        client.post()
                .uri("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .body(authorCreateRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }
}
