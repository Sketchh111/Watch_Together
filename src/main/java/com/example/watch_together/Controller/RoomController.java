package com.example.watch_together.Controller;

import java.time.LocalDateTime;
import java.util.List;
import com.example.watch_together.Model.JoinRoomRequest;
import com.example.watch_together.Model.Room;
import com.example.watch_together.Model.RoomMember;
import com.example.watch_together.Model.User;
import com.example.watch_together.Repository.RoomMemberRepository;
import com.example.watch_together.Repository.RoomRepository;
import com.example.watch_together.Repository.UserRepository;
import com.example.watch_together.Model.CreateRoomRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

        @Autowired
        private RoomRepository roomRepository;

        @Autowired
        private UserRepository userRepository;

        @PostMapping("/create")
        public ResponseEntity<?> createRoom(
                        @RequestBody CreateRoomRequest request) {

                // 1. Find creator
                User user = userRepository
                                .findByUsername(request.getUsername())
                                .orElse(null);

                if (user == null) {
                        return ResponseEntity
                                        .badRequest()
                                        .body("User not found");
                }

                // 2. Validate password
                if (request.getRoomPassword() == null
                                || request.getRoomPassword().trim().isEmpty()) {

                        return ResponseEntity
                                        .badRequest()
                                        .body("Room password is required");
                }

                // 3. Generate unique room code
                String roomCode;

                do {
                        roomCode = UUID.randomUUID()
                                        .toString()
                                        .substring(0, 6)
                                        .toUpperCase();

                } while (roomRepository.existsByRoomCode(roomCode));

                // 4. Get room name
                String roomName = request.getRoomName();

                if (roomName == null
                                || roomName.trim().isEmpty()) {

                        roomName = roomCode;
                }

                // 5. Create Room
                Room room = new Room(
                                roomCode,
                                request.getRoomPassword(),
                                user);

                // 6. Set room name
                room.setRoomName(roomName);

                // 7. Save room
                Room savedRoom = roomRepository.save(room);

                // 8. Return created room
                return ResponseEntity.ok(savedRoom);
        }

        @Autowired
        private RoomMemberRepository roomMemberRepository;

        @PostMapping("/join")
        public ResponseEntity<?> joinRoom(@RequestBody JoinRoomRequest request) {

                // Find user
                User user = userRepository
                                .findByUsername(request.getUsername())
                                .orElse(null);

                if (user == null) {
                        return ResponseEntity
                                        .badRequest()
                                        .body("User not found");
                }

                // Find room
                Room room = roomRepository
                                .findByRoomCode(request.getRoomCode())
                                .orElse(null);

                if (room == null) {
                        return ResponseEntity
                                        .badRequest()
                                        .body("Room not found");
                }

                // Check password
                if (!room.getRoomPassword().equals(request.getRoomPassword())) {
                        return ResponseEntity
                                        .badRequest()
                                        .body("Incorrect room password");
                }

                RoomMember member = roomMemberRepository
                                .findByRoomAndUser(room, user)
                                .orElse(null);

                if (member != null) {

                        // User is already inside the room
                        if (member.getLeftAt() == null) {
                                return ResponseEntity
                                                .badRequest()
                                                .body("User is already inside this room");
                        }

                        // User previously left → allow rejoining
                        member.setLeftAt(null);
                        member.setJoinedAt(LocalDateTime.now());

                        roomMemberRepository.save(member);

                        return ResponseEntity.ok("Successfully rejoined room");
                }

                RoomMember newMember = new RoomMember();

                newMember.setRoom(room);
                newMember.setUser(user);
                newMember.setJoinedAt(LocalDateTime.now());
                newMember.setLeftAt(null);

                roomMemberRepository.save(newMember);

                return ResponseEntity.ok("Successfully joined room");

        }

        @GetMapping("/{roomCode}/members")
        public ResponseEntity<?> getRoomMembers(@PathVariable String roomCode) {

                // Find room
                Room room = roomRepository
                                .findByRoomCode(roomCode)
                                .orElse(null);

                if (room == null) {
                        return ResponseEntity
                                        .badRequest()
                                        .body("Room not found");
                }

                // Find members
                List<RoomMember> members = roomMemberRepository.findByRoom(room);

                return ResponseEntity.ok(members);
        }

        @PostMapping("/leave")
        public ResponseEntity<?> leaveRoom(
                        @RequestParam String username,
                        @RequestParam String roomCode) {

                // Find user
                User user = userRepository
                                .findByUsername(username)
                                .orElse(null);

                if (user == null) {
                        return ResponseEntity
                                        .badRequest()
                                        .body("User not found");
                }

                // Find room
                Room room = roomRepository
                                .findByRoomCode(roomCode)
                                .orElse(null);

                if (room == null) {
                        return ResponseEntity
                                        .badRequest()
                                        .body("Room not found");
                }

                // Find membership
                RoomMember member = roomMemberRepository
                                .findByRoomAndUser(room, user)
                                .orElse(null);

                if (member == null) {
                        return ResponseEntity
                                        .badRequest()
                                        .body("User is not a member of this room");
                }

                // Check if already left
                if (member.getLeftAt() != null) {
                        return ResponseEntity
                                        .badRequest()
                                        .body("User has already left this room");
                }

                // Set leave time
                member.setLeftAt(LocalDateTime.now());

                roomMemberRepository.save(member);

                return ResponseEntity.ok("Successfully left room");
        }

        @GetMapping
        public ResponseEntity<List<Room>> getAllRooms() {

                List<Room> rooms = roomRepository.findAll();

                return ResponseEntity.ok(rooms);
        }
}