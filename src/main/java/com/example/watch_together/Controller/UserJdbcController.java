package com.example.watch_together.Controller;

import com.example.watch_together.Model.User;
import com.example.watch_together.Repository.UserJdbcRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-jdbc")
@CrossOrigin(origins = "*")
public class UserJdbcController {

    @Autowired
    private UserJdbcRepository userJdbcRepository;

    // CREATE
    @PostMapping
    public ResponseEntity<String> createUser(
            @RequestBody User user) {

        int result =
                userJdbcRepository.createUser(user);

        if (result > 0) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("User created successfully");
        }

        return ResponseEntity
                .badRequest()
                .body("Failed to create user");
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {

        List<User> users =
                userJdbcRepository.getAllUsers();

        return ResponseEntity.ok(users);
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable Long id) {

        try {

            User user =
                    userJdbcRepository.getUserById(id);

            return ResponseEntity.ok(user);

        } catch (Exception e) {

            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {

        int result =
                userJdbcRepository.updateUser(id, user);

        if (result > 0) {
            return ResponseEntity.ok(
                    "User updated successfully"
            );
        }

        return ResponseEntity
                .notFound()
                .build();
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id) {

        int result =
                userJdbcRepository.deleteUser(id);

        if (result > 0) {
            return ResponseEntity.ok(
                    "User deleted successfully"
            );
        }

        return ResponseEntity
                .notFound()
                .build();
    }
}