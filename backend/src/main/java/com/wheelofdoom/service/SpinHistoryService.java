package com.wheelofdoom.service;

import com.wheelofdoom.dto.SpinRequest;
import com.wheelofdoom.model.SpinHistory;
import com.wheelofdoom.model.User;
import com.wheelofdoom.repository.SpinHistoryRepository;
import com.wheelofdoom.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SpinHistoryService {

    private final SpinHistoryRepository spinHistoryRepository;
    private final UserRepository userRepository;

    public SpinHistoryService(SpinHistoryRepository spinHistoryRepository, UserRepository userRepository) {
        this.spinHistoryRepository = spinHistoryRepository;
        this.userRepository = userRepository;
    }

    public SpinHistory record(Long userId, SpinRequest request) {
        User user = userRepository.findById(userId).orElseThrow();
        SpinHistory spin = new SpinHistory();
        spin.setPickedName(request.getPickedName());
        spin.setSpunAt(LocalDateTime.now());
        spin.setUser(user);
        return spinHistoryRepository.save(spin);
    }

    public List<SpinHistory> getHistory(Long userId) {
        return spinHistoryRepository.findByUserId(userId);
    }
}
