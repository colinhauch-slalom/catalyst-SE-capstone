package com.wheelofdoom.controller;

import com.wheelofdoom.dto.SpinRequest;
import com.wheelofdoom.model.SpinHistory;
import com.wheelofdoom.model.User;
import com.wheelofdoom.repository.UserRepository;
import com.wheelofdoom.service.SpinHistoryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
class SpinHistoryControllerTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void recordReturnsCreatedSpinHistory() {
        StubSpinHistoryService spinHistoryService = new StubSpinHistoryService();
        UserRepository userRepository = mock(UserRepository.class);
        SpinHistoryController spinHistoryController = new SpinHistoryController(spinHistoryService, userRepository);
        authenticateAs("alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user(42L, "alice")));
        SpinRequest request = new SpinRequest();
        request.setPickedName("Alice");
        SpinHistory history = spinHistory(1L, "Alice");
        spinHistoryService.recordResponse = history;

        ResponseEntity<SpinHistory> response = spinHistoryController.record(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(history);
        assertThat(spinHistoryService.recordUserId).isEqualTo(42L);
        assertThat(spinHistoryService.recordRequest).isSameAs(request);
    }

    @Test
    void getHistoryReturnsCurrentUsersHistory() {
        StubSpinHistoryService spinHistoryService = new StubSpinHistoryService();
        UserRepository userRepository = mock(UserRepository.class);
        SpinHistoryController spinHistoryController = new SpinHistoryController(spinHistoryService, userRepository);
        authenticateAs("alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user(42L, "alice")));
        List<SpinHistory> history = List.of(spinHistory(1L, "Alice"));
        spinHistoryService.historyResponse = history;

        ResponseEntity<List<SpinHistory>> response = spinHistoryController.getHistory();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(history);
        assertThat(spinHistoryService.historyUserId).isEqualTo(42L);
    }

    private void authenticateAs(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null)
        );
    }

    private User user(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    private SpinHistory spinHistory(Long id, String pickedName) {
        SpinHistory history = new SpinHistory();
        history.setId(id);
        history.setPickedName(pickedName);
        history.setSpunAt(LocalDateTime.now());
        return history;
    }

    private static class StubSpinHistoryService extends SpinHistoryService {
        private Long recordUserId;
        private SpinRequest recordRequest;
        private SpinHistory recordResponse;
        private Long historyUserId;
        private List<SpinHistory> historyResponse;

        private StubSpinHistoryService() {
            super(null, null);
        }

        @Override
        public SpinHistory record(Long userId, SpinRequest request) {
            this.recordUserId = userId;
            this.recordRequest = request;
            return recordResponse;
        }

        @Override
        public List<SpinHistory> getHistory(Long userId) {
            this.historyUserId = userId;
            return historyResponse;
        }
    }
}