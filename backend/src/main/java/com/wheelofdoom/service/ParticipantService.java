package com.wheelofdoom.service;

import com.wheelofdoom.dto.ParticipantRequest;
import com.wheelofdoom.model.Participant;
import com.wheelofdoom.model.User;
import com.wheelofdoom.repository.ParticipantRepository;
import com.wheelofdoom.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;

    public ParticipantService(ParticipantRepository participantRepository, UserRepository userRepository) {
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
    }

    public List<Participant> getAll(Long userId) {
        return participantRepository.findByUserId(userId);
    }

    public Participant add(Long userId, ParticipantRequest request) {
        User user = userRepository.findById(userId).orElseThrow();
        Participant participant = new Participant();
        participant.setName(request.getName());
        participant.setActive(true);
        participant.setUser(user);
        return participantRepository.save(participant);
    }

    public Participant toggle(Long participantId, boolean active) {
        Participant participant = participantRepository.findById(participantId).orElseThrow();
        participant.setActive(active);
        return participantRepository.save(participant);
    }

    public void delete(Long participantId) {
        participantRepository.deleteById(participantId);
    }
}
