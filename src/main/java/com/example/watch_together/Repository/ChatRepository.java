package com.example.watch_together.Repository;

import com.example.watch_together.Model.ChatEntity;
import com.example.watch_together.Model.Room;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChatEntity, Long> {

    List<ChatEntity> findByRoomOrderBySentAtAsc(Room room);
}