package com.jeevan.TradingApp.controller;

import com.jeevan.TradingApp.domain.USER_ROLE;
import com.jeevan.TradingApp.exception.CustomException;
import com.jeevan.TradingApp.modal.User;
import com.jeevan.TradingApp.repository.UserRepository;
import com.jeevan.TradingApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private User getAdminUser(String jwt) {
        User user = userService.findUserProfileByJwt(jwt);
        if (user.getRole() != USER_ROLE.ROLE_ADMIN) {
            throw new CustomException("UNAUTHORIZED", "Admin access required");
        }
        return user;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader("Authorization") String jwt) {
        getAdminUser(jwt);
        List<User> users = userRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<User> approveUser(@PathVariable Long id, @RequestHeader("Authorization") String jwt) {
        getAdminUser(jwt);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setApprovedByAdmin(true);
            return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
        }
        throw new CustomException("USER_NOT_FOUND", "User not found with id: " + id);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<User> rejectUser(@PathVariable Long id, @RequestHeader("Authorization") String jwt) {
        getAdminUser(jwt);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setApprovedByAdmin(false);
            return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
        }
        throw new CustomException("USER_NOT_FOUND", "User not found with id: " + id);
    }
}
