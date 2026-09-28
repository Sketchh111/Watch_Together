package com.example.watch_together.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "room_code", nullable = false, unique = true)
    private String roomCode;

    @Column(name = "room_password", nullable = false)
    private String roomPassword;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    private LocalDateTime createdAt;

    @Column(length = 20)
    private String status;

    // Default constructor - REQUIRED by JPA
    public Room() {
    }

    // Constructor used by RoomController
    public Room(String roomCode, String roomPassword, User createdBy) {
        this.roomCode = roomCode;
        this.roomPassword = roomPassword;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    public Long getId() {
        return id;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getRoomPassword() {
        return roomPassword;
    }

    public void setRoomPassword(String roomPassword) {
        this.roomPassword = roomPassword;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}