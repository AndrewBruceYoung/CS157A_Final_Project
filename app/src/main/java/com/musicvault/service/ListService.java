package com.musicvault.service;

import com.musicvault.dto.AlbumSummaryDto;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

@Service
public class ListService {

    private final JdbcTemplate jdbcTemplate;

    public ListService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AlbumListSummaryDto> listForUser(Integer userId) {
        return jdbcTemplate.query(
                """
                        SELECT al.list_id, al.name, al.description, COUNT(la.album_id) AS album_count
                        FROM AlbumList al
                        LEFT JOIN ListAlbum la ON al.list_id = la.list_id
                        WHERE al.user_id = ?
                        GROUP BY al.list_id, al.name, al.description
                        ORDER BY al.name
                        """,
                (rs, rowNum) -> new AlbumListSummaryDto(
                        rs.getInt("list_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getLong("album_count")),
                userId);
    }

    public Optional<AlbumListDetailDto> getListDetail(Integer listId, Integer currentUserId) {
        List<AlbumListDetailDto> lists = jdbcTemplate.query(
                """
                        SELECT al.list_id, al.name, al.description, al.user_id, u.username
                        FROM AlbumList al
                        JOIN User u ON al.user_id = u.user_id
                        WHERE al.list_id = ?
                        """,
                (rs, rowNum) -> {
                    AlbumListDetailDto dto = new AlbumListDetailDto();
                    dto.setListId(rs.getInt("list_id"));
                    dto.setName(rs.getString("name"));
                    dto.setDescription(rs.getString("description"));
                    dto.setOwnerUserId(rs.getInt("user_id"));
                    dto.setOwnerUsername(rs.getString("username"));
                    dto.setOwnedByCurrentUser(
                            currentUserId != null && currentUserId.equals(rs.getInt("user_id")));
                    return dto;
                },
                listId);

        if (lists.isEmpty()) {
            return Optional.empty();
        }

        AlbumListDetailDto detail = lists.get(0);
        detail.getAlbums().addAll(jdbcTemplate.query(
                """
                        SELECT
                            la.position,
                            a.album_id,
                            a.title,
                            a.release_date,
                            rl.name AS record_label,
                            rt.name AS release_type,
                            ROUND(AVG(r.score), 1) AS avg_rating,
                            COUNT(r.user_id) AS rating_count,
                            GROUP_CONCAT(DISTINCT ar.name ORDER BY ar.name SEPARATOR ', ') AS artists,
                            GROUP_CONCAT(DISTINCT g.name ORDER BY g.name SEPARATOR ', ') AS genres
                        FROM ListAlbum la
                        JOIN Album a ON la.album_id = a.album_id
                        JOIN ReleaseType rt ON a.type_id = rt.type_id
                        LEFT JOIN RecordLabel rl ON a.label_id = rl.label_id
                        LEFT JOIN Rates r ON a.album_id = r.album_id
                        LEFT JOIN CreditedOn co ON a.album_id = co.album_id
                        LEFT JOIN Artist ar ON co.artist_id = ar.artist_id
                        LEFT JOIN AlbumGenre ag ON a.album_id = ag.album_id
                        LEFT JOIN Genre g ON ag.genre_id = g.genre_id
                        WHERE la.list_id = ?
                        GROUP BY la.position, a.album_id, a.title, a.release_date, rl.name, rt.name
                        ORDER BY la.position
                        """,
                (rs, rowNum) -> new ListAlbumDto(rs.getInt("position"), mapAlbum(rs)),
                listId));
        return Optional.of(detail);
    }

    public Integer createList(Integer userId, String name, String description) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO AlbumList (name, description, user_id) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, blankToNull(description));
            ps.setInt(3, userId);
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to create list");
        }
        return key.intValue();
    }

    public boolean updateList(Integer listId, Integer userId, String name, String description) {
        int updated = jdbcTemplate.update(
                """
                        UPDATE AlbumList
                        SET name = ?, description = ?
                        WHERE list_id = ? AND user_id = ?
                        """,
                name,
                blankToNull(description),
                listId,
                userId);
        return updated > 0;
    }

    public boolean deleteList(Integer listId, Integer userId) {
        return jdbcTemplate.update(
                "DELETE FROM AlbumList WHERE list_id = ? AND user_id = ?",
                listId,
                userId) > 0;
    }

    public boolean addAlbum(Integer listId, Integer userId, Integer albumId) {
        Integer ownerId = jdbcTemplate.query(
                "SELECT user_id FROM AlbumList WHERE list_id = ?",
                rs -> rs.next() ? rs.getInt("user_id") : null,
                listId);
        if (ownerId == null || !ownerId.equals(userId)) {
            return false;
        }

        Integer nextPosition = jdbcTemplate.query(
                "SELECT COALESCE(MAX(position), 0) + 1 AS next_pos FROM ListAlbum WHERE list_id = ?",
                rs -> rs.next() ? rs.getInt("next_pos") : 1,
                listId);

        int inserted = jdbcTemplate.update(
                """
                        INSERT IGNORE INTO ListAlbum (list_id, album_id, position)
                        VALUES (?, ?, ?)
                        """,
                listId,
                albumId,
                nextPosition);
        return inserted > 0;
    }

    public boolean removeAlbum(Integer listId, Integer userId, Integer albumId) {
        Integer ownerId = jdbcTemplate.query(
                "SELECT user_id FROM AlbumList WHERE list_id = ?",
                rs -> rs.next() ? rs.getInt("user_id") : null,
                listId);
        if (ownerId == null || !ownerId.equals(userId)) {
            return false;
        }
        return jdbcTemplate.update(
                "DELETE FROM ListAlbum WHERE list_id = ? AND album_id = ?",
                listId,
                albumId) > 0;
    }

    public List<AlbumListSummaryDto> listsOwnedBy(Integer userId) {
        return listForUser(userId);
    }

    private static AlbumSummaryDto mapAlbum(ResultSet rs) throws SQLException {
        java.sql.Date date = rs.getDate("release_date");
        LocalDate releaseDate = date != null ? date.toLocalDate() : null;
        double avg = rs.getDouble("avg_rating");
        Double avgRating = rs.wasNull() ? null : avg;
        return new AlbumSummaryDto(
                rs.getInt("album_id"),
                rs.getString("title"),
                releaseDate,
                rs.getString("record_label"),
                rs.getString("release_type"),
                avgRating,
                rs.getLong("rating_count"),
                rs.getString("artists"),
                rs.getString("genres"));
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public record AlbumListSummaryDto(Integer listId, String name, String description, Long albumCount) {
    }

    public record ListAlbumDto(Integer position, AlbumSummaryDto album) {
    }

    public static class AlbumListDetailDto {
        private Integer listId;
        private String name;
        private String description;
        private Integer ownerUserId;
        private String ownerUsername;
        private boolean ownedByCurrentUser;
        private final java.util.ArrayList<ListAlbumDto> albums = new java.util.ArrayList<>();

        public Integer getListId() {
            return listId;
        }

        public void setListId(Integer listId) {
            this.listId = listId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getOwnerUserId() {
            return ownerUserId;
        }

        public void setOwnerUserId(Integer ownerUserId) {
            this.ownerUserId = ownerUserId;
        }

        public String getOwnerUsername() {
            return ownerUsername;
        }

        public void setOwnerUsername(String ownerUsername) {
            this.ownerUsername = ownerUsername;
        }

        public boolean isOwnedByCurrentUser() {
            return ownedByCurrentUser;
        }

        public void setOwnedByCurrentUser(boolean ownedByCurrentUser) {
            this.ownedByCurrentUser = ownedByCurrentUser;
        }

        public java.util.List<ListAlbumDto> getAlbums() {
            return albums;
        }
    }
}
