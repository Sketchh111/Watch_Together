package com.example.watch_together.Controller;

import com.example.watch_together.Model.User;
import com.example.watch_together.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        // Check username
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body("Username already exists");
        }

        // Check email
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body("Email already exists");
        }

        // Save user
        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser) {

        User user = userRepository
                .findByUsername(loginUser.getUsername())
                .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Invalid username or password");
        }

        if (!user.getPassword().equals(loginUser.getPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body("Invalid username or password");
        }

        return ResponseEntity.ok(user);
    }
}
