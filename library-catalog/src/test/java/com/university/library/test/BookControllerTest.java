package com.university.library.test;

import com.university.library.controller.dto.BookCreateRequest;
import com.university.library.controller.dto.BookResponse;
import com.university.library.exception.BookNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import com.university.library.controller.BookController;
import com.university.library.controller.BookMapper;
import com.university.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.springframework.http.MediaType;
import static org.mockito.Mockito.when;

@WebMvcTest(BookController.class)
@AutoConfigureRestTestClient
public class BookControllerTest {
    @Autowired
    RestTestClient client;

    @MockitoBean
    BookService bookService;

    @MockitoBean
    BookMapper bookMapper;

    @Test
    void findById_shouldReturn200_whenBookExists() {
        UUID id = UUID.randomUUID();
        BookResponse response = new BookResponse(id, "title", "978-5-04-116820-7", 1869, "authorName", 4);

        when(bookService.findByIdWithAuthor(id)).thenReturn(Optional.of(response));

        client.get()
                .uri("/api/books/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookResponse.class)
                .value(book -> assertThat(book.title()).isEqualTo("title"));
    }

    @Test
    void findById_shouldReturn404_whenBookNotFound() {
        UUID id = UUID.randomUUID();
        when(bookService.findByIdWithAuthor(id)).thenReturn(Optional.empty());

        client.get()
                .uri("/api/books/{id}", id)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void save_shouldReturn400_whenTitleIsBlank() {
        BookCreateRequest request = new BookCreateRequest(
                "",  // пустой title
                "978-5-04-116820-7",
                1869,
                UUID.randomUUID(),
                5
        );

        client.post()
                .uri("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void findById_shouldReturn404WithApiError_whenBookNotFound() {
        UUID id = UUID.randomUUID();
        when(bookService.findByIdWithAuthor(id)).thenThrow(new BookNotFoundException(id));

        client.get()
                .uri("/api/books/{id}", id)
                .exchange()
                .expectStatus().isNotFound();
    }
}
