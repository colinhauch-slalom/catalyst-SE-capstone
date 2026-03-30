package com.wheelofdoom.controller;

import com.wheelofdoom.dto.ParticipantRequest;
import com.wheelofdoom.dto.ParticipantToggleRequest;
import com.wheelofdoom.model.Participant;
import com.wheelofdoom.model.User;
import com.wheelofdoom.repository.UserRepository;
import com.wheelofdoom.service.ParticipantService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
class ParticipantControllerTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllReturnsParticipantsForCurrentUser() {
        StubParticipantService participantService = new StubParticipantService();
        UserRepository userRepository = mock(UserRepository.class);
        ParticipantController participantController = new ParticipantController(participantService, userRepository);
        authenticateAs("alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user(42L, "alice")));
        List<Participant> participants = List.of(participant(1L, "Alice", true));
        participantService.getAllResponse = participants;

        ResponseEntity<List<Participant>> response = participantController.getAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(participants);
        assertThat(participantService.getAllUserId).isEqualTo(42L);
    }

    @Test
    void addReturnsCreatedParticipant() {
        StubParticipantService participantService = new StubParticipantService();
        UserRepository userRepository = mock(UserRepository.class);
        ParticipantController participantController = new ParticipantController(participantService, userRepository);
        authenticateAs("alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user(42L, "alice")));
        ParticipantRequest request = new ParticipantRequest();
        request.setName("Alice");
        Participant participant = participant(1L, "Alice", true);
        participantService.addResponse = participant;

        ResponseEntity<Participant> response = participantController.add(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(participant);
        assertThat(participantService.addUserId).isEqualTo(42L);
        assertThat(participantService.addRequest).isSameAs(request);
    }

    @Test
    void toggleReturnsUpdatedParticipant() {
        StubParticipantService participantService = new StubParticipantService();
        ParticipantController participantController = new ParticipantController(participantService, mock(UserRepository.class));
        ParticipantToggleRequest request = new ParticipantToggleRequest();
        request.setActive(false);
        Participant participant = participant(1L, "Alice", false);
        participantService.toggleResponse = participant;

        ResponseEntity<Participant> response = participantController.toggle(1L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(participant);
        assertThat(participantService.toggleParticipantId).isEqualTo(1L);
        assertThat(participantService.toggleActive).isFalse();
    }

    @Test
    void deleteReturnsNoContent() {
        StubParticipantService participantService = new StubParticipantService();
        ParticipantController participantController = new ParticipantController(participantService, mock(UserRepository.class));
        ResponseEntity<Void> response = participantController.delete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(participantService.deletedParticipantId).isEqualTo(1L);
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

    private Participant participant(Long id, String name, boolean active) {
        Participant participant = new Participant();
        participant.setId(id);
        participant.setName(name);
        participant.setActive(active);
        return participant;
    }

    private static class StubParticipantService extends ParticipantService {
        private Long getAllUserId;
        private List<Participant> getAllResponse;
        private Long addUserId;
        private ParticipantRequest addRequest;
        private Participant addResponse;
        private Long toggleParticipantId;
        private boolean toggleActive;
        private Participant toggleResponse;
        private Long deletedParticipantId;

        private StubParticipantService() {
            super(null, null);
        }

        @Override
        public List<Participant> getAll(Long userId) {
            this.getAllUserId = userId;
            return getAllResponse;
        }

        @Override
        public Participant add(Long userId, ParticipantRequest request) {
            this.addUserId = userId;
            this.addRequest = request;
            return addResponse;
        }

        @Override
        public Participant toggle(Long participantId, boolean active) {
            this.toggleParticipantId = participantId;
            this.toggleActive = active;
            return toggleResponse;
        }

        @Override
        public void delete(Long participantId) {
            this.deletedParticipantId = participantId;
        }
    }
}