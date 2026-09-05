package com.example.watch_together.Repository;

import com.example.watch_together.Model.Room;
import com.example.watch_together.Model.User;
import com.example.watch_together.Model.RoomMember;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {

    Optional<RoomMember> findByRoomAndUser(Room room, User user);

    List<RoomMember> findByRoom(Room room);

    boolean existsByRoomAndUser(Room room, User user);
}