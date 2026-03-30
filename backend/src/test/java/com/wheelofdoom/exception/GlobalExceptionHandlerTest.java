package com.wheelofdoom.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleUsernameAlreadyTakenReturnsConflict() {
        UsernameAlreadyTakenException exception = new UsernameAlreadyTakenException("alice");

        ResponseEntity<String> response = globalExceptionHandler.handleUsernameAlreadyTaken(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isEqualTo("Username already taken: alice");
    }

    @Test
    void handleIllegalArgumentReturnsUnauthorized() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid credentials");

        ResponseEntity<String> response = globalExceptionHandler.handleIllegalArgument(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo("Invalid credentials");
    }
}