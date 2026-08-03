package com.musicvault.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final JdbcTemplate jdbcTemplate;

    public CustomUserDetailsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserRow> rows = jdbcTemplate.query(
                """
                        SELECT u.username, u.password_hash,
                               CASE WHEN a.user_id IS NULL THEN 0 ELSE 1 END AS is_admin
                        FROM User u
                        LEFT JOIN Administrator a ON u.user_id = a.user_id
                        WHERE u.username = ?
                        """,
                (rs, rowNum) -> new UserRow(
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getInt("is_admin") == 1),
                username);

        if (rows.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        UserRow row = rows.get(0);
        List<String> roles = new ArrayList<>();
        roles.add("USER");
        if (row.admin()) {
            roles.add("ADMIN");
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(row.username())
                .password(row.passwordHash())
                .roles(roles.toArray(String[]::new))
                .build();
    }

    private record UserRow(String username, String passwordHash, boolean admin) {
    }
}
