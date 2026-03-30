package com.wheelofdoom.service;

import com.wheelofdoom.dto.SpinRequest;
import com.wheelofdoom.model.SpinHistory;
import com.wheelofdoom.model.User;
import com.wheelofdoom.repository.SpinHistoryRepository;
import com.wheelofdoom.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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
class SpinHistoryServiceTest {

    @Mock
    private SpinHistoryRepository spinHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SpinHistoryService spinHistoryService;

    @Test
    void recordCreatesSpinHistoryEntryForExistingUser() {
        User user = new User();
        user.setId(42L);
        user.setUsername("owner");
        SpinRequest request = spinRequest("Alice");
        when(userRepository.findById(42L)).thenReturn(Optional.of(user));
        when(spinHistoryRepository.save(any(SpinHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));
        LocalDateTime before = LocalDateTime.now();

        SpinHistory spinHistory = spinHistoryService.record(42L, request);

        LocalDateTime after = LocalDateTime.now();
        ArgumentCaptor<SpinHistory> savedHistory = ArgumentCaptor.forClass(SpinHistory.class);
        verify(spinHistoryRepository).save(savedHistory.capture());
        assertThat(savedHistory.getValue().getPickedName()).isEqualTo("Alice");
        assertThat(savedHistory.getValue().getUser()).isSameAs(user);
        assertThat(savedHistory.getValue().getSpunAt()).isNotNull();
        assertThat(savedHistory.getValue().getSpunAt()).isBetween(before, after);
        assertThat(spinHistory.getPickedName()).isEqualTo("Alice");
    }

    @Test
    void recordThrowsWhenUserDoesNotExist() {
        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> spinHistoryService.record(42L, spinRequest("Alice")))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getHistoryReturnsSavedHistoryForUser() {
        SpinHistory first = new SpinHistory();
        first.setPickedName("Alice");
        SpinHistory second = new SpinHistory();
        second.setPickedName("Bob");
        when(spinHistoryRepository.findByUserId(42L)).thenReturn(List.of(first, second));

        List<SpinHistory> history = spinHistoryService.getHistory(42L);

        assertThat(history).containsExactly(first, second);
    }

    private SpinRequest spinRequest(String pickedName) {
        SpinRequest request = new SpinRequest();
        request.setPickedName(pickedName);
        return request;
    }
}
