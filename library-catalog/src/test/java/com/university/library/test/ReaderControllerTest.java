package com.university.library.test;

import com.university.library.controller.ReaderController;
import com.university.library.controller.ReaderMapper;
import com.university.library.controller.dto.ReaderResponse;
import com.university.library.model.Reader;
import com.university.library.service.ReaderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@WebMvcTest(ReaderController.class)
@AutoConfigureRestTestClient
public class ReaderControllerTest {
    @Autowired
    RestTestClient client;

    @MockitoBean
    ReaderService readerService;

    @MockitoBean
    ReaderMapper readerMapper;

    @Test
    void findById_shouldReturn200_whenReaderExists() {
        UUID id = UUID.randomUUID();
        Reader reader = new Reader(id, "fullName", "mail@mail.ru", LocalDate.now());
        ReaderResponse response = new ReaderResponse(id, "fullName", "mail@mail.ru", LocalDate.now());

        when(readerService.findById(id)).thenReturn(Optional.of(reader));
        when(readerMapper.toResponse(reader)).thenReturn(response);

        client.get()
                .uri("/api/readers/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ReaderResponse.class)
                .value(readerResponse -> assertThat(readerResponse.fullName()).isEqualTo("fullName"));
    }

    @Test
    void findById_shouldReturn404_whenReaderDoesNotFound() {
        UUID id = UUID.randomUUID();
        when(readerService.findById(id)).thenReturn(Optional.empty());

        client.get()
                .uri("/api/readers/{id}", id)
                .exchange()
                .expectStatus().isNotFound();
    }
}
