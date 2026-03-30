package com.wheelofdoom.service;

import com.wheelofdoom.dto.AuthRequest;
import com.wheelofdoom.dto.AuthResponse;
import com.wheelofdoom.exception.UsernameAlreadyTakenException;
import com.wheelofdoom.model.User;
import com.wheelofdoom.repository.UserRepository;
import com.wheelofdoom.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private JwtUtil jwtUtil;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3_600_000L);
        authService = new AuthService(userRepository, passwordEncoder, jwtUtil);
    }

    @Test
    void registerSavesEncodedPasswordWhenUsernameIsAvailable() {
        AuthRequest request = authRequest("alice", "password123");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

        authService.register(request);

        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(savedUser.capture());
        assertThat(savedUser.getValue().getUsername()).isEqualTo("alice");
        assertThat(savedUser.getValue().getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void registerThrowsWhenUsernameAlreadyExists() {
        AuthRequest request = authRequest("alice", "password123");
        User existingUser = new User();
        existingUser.setUsername("alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UsernameAlreadyTakenException.class)
                .hasMessage("Username already taken: alice");

        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void loginReturnsJwtWhenCredentialsAreValid() {
        AuthRequest request = authRequest("alice", "password123");
        User user = new User();
        user.setUsername("alice");
        user.setPassword("encoded-password");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);

        AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isNotBlank();
        assertThat(jwtUtil.validateToken(response.getToken())).isTrue();
        assertThat(jwtUtil.extractUsername(response.getToken())).isEqualTo("alice");
    }

    @Test
    void loginThrowsWhenUserDoesNotExist() {
        AuthRequest request = authRequest("alice", "password123");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid credentials");

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void loginThrowsWhenPasswordDoesNotMatch() {
        AuthRequest request = authRequest("alice", "password123");
        User user = new User();
        user.setUsername("alice");
        user.setPassword("encoded-password");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid credentials");
    }

    private AuthRequest authRequest(String username, String password) {
        AuthRequest request = new AuthRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }
}
