package com.wheelofdoom.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "spin_history")
public class SpinHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pickedName;

    @Column(nullable = false)
    private LocalDateTime spunAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPickedName() { return pickedName; }
    public void setPickedName(String pickedName) { this.pickedName = pickedName; }

    public LocalDateTime getSpunAt() { return spunAt; }
    public void setSpunAt(LocalDateTime spunAt) { this.spunAt = spunAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
