package com.wheelofdoom.controller;

import com.wheelofdoom.dto.AuthRequest;
import com.wheelofdoom.dto.AuthResponse;
import com.wheelofdoom.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
class AuthControllerTest {

    @Test
    void registerReturnsCreatedStatus() {
        StubAuthService authService = new StubAuthService();
        AuthController authController = new AuthController(authService);
        AuthRequest request = authRequest("alice", "secret123");

        ResponseEntity<Void> response = authController.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(authService.registerRequest).isSameAs(request);
    }

    @Test
    void loginReturnsTokenResponse() {
        StubAuthService authService = new StubAuthService();
        AuthController authController = new AuthController(authService);
        AuthRequest request = authRequest("alice", "secret123");
        AuthResponse authResponse = new AuthResponse("jwt-token");
        authService.loginResponse = authResponse;

        ResponseEntity<AuthResponse> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(authResponse);
        assertThat(authService.loginRequest).isSameAs(request);
    }

    private AuthRequest authRequest(String username, String password) {
        AuthRequest request = new AuthRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }

    private static class StubAuthService extends AuthService {
        private AuthRequest registerRequest;
        private AuthRequest loginRequest;
        private AuthResponse loginResponse;

        private StubAuthService() {
            super(null, null, null);
        }

        @Override
        public void register(AuthRequest request) {
            this.registerRequest = request;
        }

        @Override
        public AuthResponse login(AuthRequest request) {
            this.loginRequest = request;
            return loginResponse;
        }
    }
}