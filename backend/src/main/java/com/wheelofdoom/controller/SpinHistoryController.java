package com.wheelofdoom.controller;

import com.wheelofdoom.dto.SpinRequest;
import com.wheelofdoom.model.SpinHistory;
import com.wheelofdoom.repository.UserRepository;
import com.wheelofdoom.service.SpinHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spins")
public class SpinHistoryController {

    private final SpinHistoryService spinHistoryService;
    private final UserRepository userRepository;

    public SpinHistoryController(SpinHistoryService spinHistoryService, UserRepository userRepository) {
        this.spinHistoryService = spinHistoryService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<SpinHistory> record(@RequestBody SpinRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(spinHistoryService.record(currentUserId(), request));
    }

    @GetMapping
    public ResponseEntity<List<SpinHistory>> getHistory() {
        return ResponseEntity.ok(spinHistoryService.getHistory(currentUserId()));
    }

    private Long currentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow().getId();
    }
}
