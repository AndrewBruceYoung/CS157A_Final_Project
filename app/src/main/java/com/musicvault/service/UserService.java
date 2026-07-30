package com.musicvault.service;

import com.musicvault.dto.RegisterForm;
import com.musicvault.model.RegisteredUser;
import com.musicvault.model.User;
import com.musicvault.repository.RegisteredUserRepository;
import com.musicvault.repository.UserRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RegisteredUserRepository registeredUserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RegisteredUserRepository registeredUserRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.registeredUserRepository = registeredUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public Optional<Integer> findUserIdByUsername(String username) {
        return userRepository.findByUsername(username).map(user -> user.getUserId());
    }

    @Transactional
    public User register(RegisterForm form) {
        User user = new User();
        user.setUsername(form.getUsername().trim());
        user.setEmail(form.getEmail().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        user.setJoinDate(LocalDate.now());
        userRepository.save(user);

        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setUser(user);
        registeredUser.setBio(form.getBio());
        registeredUserRepository.save(registeredUser);

        return user;
    }
}
