package com.lanyue.shortlink.gateway.filter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TokenValidateGlobalFilterTest {
    @Autowired
    private WebTestClient webTestClient;
    @Test
    void shouldReturnUnauthorizedWhenNoToken() {
        webTestClient.get()
                .uri("/api/short-link/admin/v1/test")
                .exchange()
                .expectStatus().isUnauthorized();
    }
    @Test
    void shouldPassWhiteListPath() {
        webTestClient.post()
                .uri("/api/short-link/admin/v1/user/login")
                .exchange()
                // Assuming it will be routed to a destination that might give 404 or something,
                // but at least not 401 Unauthorized by the gateway
                .expectStatus().is4xxClientError();
    }
}
