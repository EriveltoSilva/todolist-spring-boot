package com.eriveltosilva.todolist.user;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel userModel) {
        UserModel user = this.userRepository.findByUsername(userModel.getUsername());
        if (user != null)
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Usuario já existe");

        userModel.setPassword(createHashedPassword(userModel.getPassword()));
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userRepository.save(userModel));
    }

    private String createHashedPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }
}
