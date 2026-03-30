package com.wheelofdoom.service;

import com.wheelofdoom.dto.ParticipantRequest;
import com.wheelofdoom.model.Participant;
import com.wheelofdoom.model.User;
import com.wheelofdoom.repository.ParticipantRepository;
import com.wheelofdoom.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ParticipantService participantService;

    @Test
    void getAllReturnsParticipantsForUser() {
        Participant active = new Participant();
        active.setName("Alice");
        Participant inactive = new Participant();
        inactive.setName("Bob");
        when(participantRepository.findByUserId(42L)).thenReturn(List.of(active, inactive));

        List<Participant> participants = participantService.getAll(42L);

        assertThat(participants).containsExactly(active, inactive);
    }

    @Test
    void addCreatesActiveParticipantForExistingUser() {
        User user = new User();
        user.setId(42L);
        user.setUsername("owner");
        ParticipantRequest request = participantRequest("Alice");
        when(userRepository.findById(42L)).thenReturn(Optional.of(user));
        when(participantRepository.save(any(Participant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Participant participant = participantService.add(42L, request);

        ArgumentCaptor<Participant> savedParticipant = ArgumentCaptor.forClass(Participant.class);
        verify(participantRepository).save(savedParticipant.capture());
        assertThat(savedParticipant.getValue().getName()).isEqualTo("Alice");
        assertThat(savedParticipant.getValue().isActive()).isTrue();
        assertThat(savedParticipant.getValue().getUser()).isSameAs(user);
        assertThat(participant.getName()).isEqualTo("Alice");
    }

    @Test
    void addThrowsWhenUserDoesNotExist() {
        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> participantService.add(42L, participantRequest("Alice")))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void toggleUpdatesActiveState() {
        Participant participant = new Participant();
        participant.setId(7L);
        participant.setName("Alice");
        participant.setActive(true);
        when(participantRepository.findById(7L)).thenReturn(Optional.of(participant));
        when(participantRepository.save(any(Participant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Participant updatedParticipant = participantService.toggle(7L, false);

        assertThat(updatedParticipant.isActive()).isFalse();
        verify(participantRepository).save(participant);
    }

    @Test
    void deleteDelegatesToRepository() {
        participantService.delete(7L);

        verify(participantRepository).deleteById(7L);
    }

    private ParticipantRequest participantRequest(String name) {
        ParticipantRequest request = new ParticipantRequest();
        request.setName(name);
        return request;
    }
}
