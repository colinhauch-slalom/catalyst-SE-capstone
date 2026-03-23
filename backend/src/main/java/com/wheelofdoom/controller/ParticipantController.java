package com.wheelofdoom.controller;

import com.wheelofdoom.dto.ParticipantRequest;
import com.wheelofdoom.dto.ParticipantToggleRequest;
import com.wheelofdoom.model.Participant;
import com.wheelofdoom.repository.UserRepository;
import com.wheelofdoom.service.ParticipantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participants")
public class ParticipantController {

    private final ParticipantService participantService;
    private final UserRepository userRepository;

    public ParticipantController(ParticipantService participantService, UserRepository userRepository) {
        this.participantService = participantService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<Participant>> getAll() {
        return ResponseEntity.ok(participantService.getAll(currentUserId()));
    }

    @PostMapping
    public ResponseEntity<Participant> add(@RequestBody ParticipantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(participantService.add(currentUserId(), request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Participant> toggle(@PathVariable Long id,
                                              @RequestBody ParticipantToggleRequest request) {
        return ResponseEntity.ok(participantService.toggle(id, request.isActive()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        participantService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Long currentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow().getId();
    }
}
