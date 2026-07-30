package com.musicvault;

import com.musicvault.model.User;
import com.musicvault.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserLoginIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void aliceCanAuthenticate() {
        User user = userRepository.findByUsername("alice").orElseThrow();
        System.out.println("loaded hash: " + user.getPasswordHash());
        System.out.println("matches: " + passwordEncoder.matches("password", user.getPasswordHash()));
        assertNotNull(user.getPasswordHash());
        assertTrue(passwordEncoder.matches("password", user.getPasswordHash()));
    }
}
