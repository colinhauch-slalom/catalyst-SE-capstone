package com.wheelofdoom.service;

import com.wheelofdoom.dto.AuthRequest;
import com.wheelofdoom.model.User;
import com.wheelofdoom.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:auth-persistence;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class AuthPersistenceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registerPersistsEncryptedPassword() {
        AuthRequest request = authRequest("integration-alice", "password123");

        authService.register(request);

        User savedUser = userRepository.findByUsername("integration-alice").orElseThrow();
        assertThat(savedUser.getPassword()).isNotEqualTo("password123");
        assertThat(savedUser.getPassword()).startsWith("$2");
        assertThat(passwordEncoder.matches("password123", savedUser.getPassword())).isTrue();
    }

    @Test
    void seededDemoUserPasswordIsStoredEncrypted() {
        User demoUser = userRepository.findByUsername("demo").orElseThrow();

        assertThat(demoUser.getPassword()).isNotEqualTo("demo");
        assertThat(demoUser.getPassword()).startsWith("$2");
        assertThat(passwordEncoder.matches("demo", demoUser.getPassword())).isTrue();
    }

    private AuthRequest authRequest(String username, String password) {
        AuthRequest request = new AuthRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }
}