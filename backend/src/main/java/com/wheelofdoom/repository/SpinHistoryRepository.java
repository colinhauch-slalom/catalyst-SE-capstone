package com.wheelofdoom.repository;

import com.wheelofdoom.model.SpinHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpinHistoryRepository extends JpaRepository<SpinHistory, Long> {
    List<SpinHistory> findByUserId(Long userId);
}
