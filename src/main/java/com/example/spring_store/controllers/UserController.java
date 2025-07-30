package com.example.spring_store.controllers;

import com.example.spring_store.entities.User;
import com.example.spring_store.services.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final FileStorageService fileStorageService;

    public UserController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

//    @GetMapping
//    public List<User> getUsers(){
//        return fileStorageService.readUsers();
//
//    }

    @GetMapping
    public ResponseEntity<?> getUsers() {
        try {
            List<User> users = fileStorageService.readUsers();
            return ResponseEntity.ok(users);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading user data: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody User user){
        try {
            List<User> users = fileStorageService.readUsers();

            // Assign a new unique ID
            long newId = users.stream()
                    .mapToLong(User::getId)
                    .max()
                    .orElse(0) + 1;
            user.setId(newId);

            users.add(user);
            fileStorageService.writeUsers(users);

            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        }
        catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding user! - " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser){
        try{
            List<User> users = fileStorageService.readUsers();

            Optional<User> existingUserOpt = users.stream()
                    .filter(user -> user.getId().equals(id))
                    .findFirst();

            if (existingUserOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User with ID " + id + " not found.");
            }

            User existingUser = existingUserOpt.get();
            existingUser.setName(updatedUser.getName());
            existingUser.setEmail(updatedUser.getEmail());

            fileStorageService.writeUsers(users);

            return ResponseEntity.ok(existingUser);
        }
        catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating user! - " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            List<User> users = fileStorageService.readUsers();

            boolean removed = users.removeIf(user -> user.getId().equals(id));

            if (!removed) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User with ID " + id + " not found.");
            }

            fileStorageService.writeUsers(users);
            return ResponseEntity.ok("User with ID " + id + " deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting user! - " + e.getMessage());
        }
    }

}
