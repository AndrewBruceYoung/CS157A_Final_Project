package com.musicvault.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class FollowService {

    private final JdbcTemplate jdbcTemplate;

    public FollowService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<UserSummaryDto> listUsers(Integer currentUserId) {
        return jdbcTemplate.query(
                """
                        SELECT
                            u.user_id,
                            u.username,
                            ru.bio,
                            u.join_date,
                            (SELECT COUNT(*) FROM Follows f WHERE f.followee_id = u.user_id) AS follower_count,
                            (SELECT COUNT(*) FROM Follows f WHERE f.follower_id = u.user_id) AS following_count,
                            CASE
                                WHEN ? IS NULL THEN 0
                                WHEN EXISTS (
                                    SELECT 1 FROM Follows f
                                    WHERE f.follower_id = ? AND f.followee_id = u.user_id
                                ) THEN 1
                                ELSE 0
                            END AS is_following
                        FROM User u
                        JOIN RegisteredUser ru ON u.user_id = ru.user_id
                        ORDER BY u.username
                        """,
                (rs, rowNum) -> new UserSummaryDto(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("bio"),
                        rs.getDate("join_date").toLocalDate(),
                        rs.getLong("follower_count"),
                        rs.getLong("following_count"),
                        rs.getInt("is_following") == 1),
                currentUserId,
                currentUserId);
    }

    public Optional<UserProfileDto> getProfile(Integer userId, Integer currentUserId) {
        List<UserProfileDto> profiles = jdbcTemplate.query(
                """
                        SELECT
                            u.user_id,
                            u.username,
                            ru.bio,
                            u.join_date,
                            (SELECT COUNT(*) FROM Follows f WHERE f.followee_id = u.user_id) AS follower_count,
                            (SELECT COUNT(*) FROM Follows f WHERE f.follower_id = u.user_id) AS following_count,
                            CASE
                                WHEN ? IS NULL THEN 0
                                WHEN EXISTS (
                                    SELECT 1 FROM Follows f
                                    WHERE f.follower_id = ? AND f.followee_id = u.user_id
                                ) THEN 1
                                ELSE 0
                            END AS is_following
                        FROM User u
                        JOIN RegisteredUser ru ON u.user_id = ru.user_id
                        WHERE u.user_id = ?
                        """,
                (rs, rowNum) -> new UserProfileDto(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("bio"),
                        rs.getDate("join_date").toLocalDate(),
                        rs.getLong("follower_count"),
                        rs.getLong("following_count"),
                        rs.getInt("is_following") == 1,
                        currentUserId != null && currentUserId.equals(rs.getInt("user_id"))),
                currentUserId,
                currentUserId,
                userId);

        if (profiles.isEmpty()) {
            return Optional.empty();
        }

        UserProfileDto profile = profiles.get(0);
        profile.getFollowers().addAll(listFollowConnections(userId, true));
        profile.getFollowingUsers().addAll(listFollowConnections(userId, false));
        return Optional.of(profile);
    }

    public boolean follow(Integer followerId, Integer followeeId) {
        if (followerId.equals(followeeId)) {
            return false;
        }
        int inserted = jdbcTemplate.update(
                """
                        INSERT IGNORE INTO Follows (follower_id, followee_id, since)
                        VALUES (?, ?, ?)
                        """,
                followerId,
                followeeId,
                LocalDate.now());
        return inserted > 0;
    }

    public boolean unfollow(Integer followerId, Integer followeeId) {
        return jdbcTemplate.update(
                "DELETE FROM Follows WHERE follower_id = ? AND followee_id = ?",
                followerId,
                followeeId) > 0;
    }

    private List<FollowConnectionDto> listFollowConnections(Integer userId, boolean followers) {
        String sql = followers
                ? """
                        SELECT u.user_id, u.username, f.since
                        FROM Follows f
                        JOIN User u ON f.follower_id = u.user_id
                        WHERE f.followee_id = ?
                        ORDER BY u.username
                        """
                : """
                        SELECT u.user_id, u.username, f.since
                        FROM Follows f
                        JOIN User u ON f.followee_id = u.user_id
                        WHERE f.follower_id = ?
                        ORDER BY u.username
                        """;
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new FollowConnectionDto(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getDate("since") != null ? rs.getDate("since").toLocalDate() : null),
                userId);
    }

    public record UserSummaryDto(
            Integer userId,
            String username,
            String bio,
            LocalDate joinDate,
            Long followerCount,
            Long followingCount,
            boolean currentlyFollowing) {
    }

    public record FollowConnectionDto(Integer userId, String username, LocalDate since) {
    }

    public static class UserProfileDto {
        private final Integer userId;
        private final String username;
        private final String bio;
        private final LocalDate joinDate;
        private final Long followerCount;
        private final Long followingCount;
        private final boolean currentlyFollowing;
        private final boolean ownProfile;
        private final java.util.ArrayList<FollowConnectionDto> followers = new java.util.ArrayList<>();
        private final java.util.ArrayList<FollowConnectionDto> followingUsers = new java.util.ArrayList<>();

        public UserProfileDto(
                Integer userId,
                String username,
                String bio,
                LocalDate joinDate,
                Long followerCount,
                Long followingCount,
                boolean currentlyFollowing,
                boolean ownProfile) {
            this.userId = userId;
            this.username = username;
            this.bio = bio;
            this.joinDate = joinDate;
            this.followerCount = followerCount;
            this.followingCount = followingCount;
            this.currentlyFollowing = currentlyFollowing;
            this.ownProfile = ownProfile;
        }

        public Integer getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getBio() {
            return bio;
        }

        public LocalDate getJoinDate() {
            return joinDate;
        }

        public Long getFollowerCount() {
            return followerCount;
        }

        public Long getFollowingCount() {
            return followingCount;
        }

        public boolean isCurrentlyFollowing() {
            return currentlyFollowing;
        }

        public boolean isOwnProfile() {
            return ownProfile;
        }

        public java.util.List<FollowConnectionDto> getFollowers() {
            return followers;
        }

        public java.util.List<FollowConnectionDto> getFollowingUsers() {
            return followingUsers;
        }
    }
}
