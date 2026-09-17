package com.example.watch_together.Controller;

import com.example.watch_together.Model.User;
import com.example.watch_together.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-crud")
@CrossOrigin(origins = "*")
public class UserCrudController {

    @Autowired
    private UserRepository userRepository;


    // ==============================
    // CREATE USER
    // ==============================

    @PostMapping
    public ResponseEntity<User> createUser(
            @RequestBody User user) {

        User savedUser =
                userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedUser);
    }


    // ==============================
    // READ ALL USERS
    // ==============================

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {

        List<User> users =
                userRepository.findAll();
                

        return ResponseEntity.ok(users);
    }


    // ==============================
    // READ USER BY ID
    // ==============================

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable Long id) {

        return userRepository
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(
                    ResponseEntity
                        .notFound()
                        .build()
                );
    }


    // ==============================
    // UPDATE USER
    // ==============================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody User updatedUser) {

        return userRepository
                .findById(id)
                .map(user -> {

                    user.setUsername(
                            updatedUser.getUsername()
                    );

                    user.setEmail(
                            updatedUser.getEmail()
                    );

                    user.setPassword(
                            updatedUser.getPassword()
                    );

                    User savedUser =
                            userRepository.save(user);

                    return ResponseEntity.ok(savedUser);

                })
                .orElse(
                    ResponseEntity
                        .notFound()
                        .build()
                );
    }


    // ==============================
    // DELETE USER
    // ==============================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id) {

        if (!userRepository.existsById(id)) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        userRepository.deleteById(id);

        return ResponseEntity.ok(
                "User deleted successfully"
        );
    }
}