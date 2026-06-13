package com.university.library.test;

import com.university.library.controller.LoanController;
import com.university.library.service.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.UUID;

@WebMvcTest(LoanController.class)
@AutoConfigureRestTestClient
public class LoanControllerTest {
    @Autowired
    RestTestClient client;

    @MockitoBean
    LoanService loanService;

    @Test
    void issueLoan_shouldReturn201_ifCopiesAvailable() {
        UUID readerId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        client.post()
                .uri("/api/loans?bookId={bookId}&readerId={readerId}", bookId, readerId)
                .exchange()
                .expectStatus().isCreated();
    }
}
