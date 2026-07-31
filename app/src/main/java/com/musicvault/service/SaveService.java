package com.musicvault.service;

import com.musicvault.dto.AlbumSummaryDto;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SaveService {

    public static final List<String> STATUSES = List.of("Owned", "Favorite", "Wishlist");

    private final JdbcTemplate jdbcTemplate;

    public SaveService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Set<String> getStatusesForAlbum(Integer userId, Integer albumId) {
        return jdbcTemplate.query(
                        "SELECT status FROM Saves WHERE user_id = ? AND album_id = ?",
                        (rs, rowNum) -> rs.getString("status"),
                        userId,
                        albumId)
                .stream()
                .collect(Collectors.toSet());
    }

    public void addSave(Integer userId, Integer albumId, String status) {
        validateStatus(status);
        jdbcTemplate.update(
                """
                        INSERT IGNORE INTO Saves (user_id, album_id, status)
                        VALUES (?, ?, ?)
                        """,
                userId,
                albumId,
                status);
    }

    public void removeSave(Integer userId, Integer albumId, String status) {
        validateStatus(status);
        jdbcTemplate.update(
                "DELETE FROM Saves WHERE user_id = ? AND album_id = ? AND status = ?",
                userId,
                albumId,
                status);
    }

    public List<SavedAlbumDto> listSaves(Integer userId, String statusFilter) {
        StringBuilder sql = new StringBuilder("""
                SELECT
                    s.status,
                    a.album_id,
                    a.title,
                    a.release_date,
                    rl.name AS record_label,
                    rt.name AS release_type,
                    ROUND(AVG(r.score), 1) AS avg_rating,
                    COUNT(r.user_id) AS rating_count,
                    GROUP_CONCAT(DISTINCT ar.name ORDER BY ar.name SEPARATOR ', ') AS artists,
                    GROUP_CONCAT(DISTINCT g.name ORDER BY g.name SEPARATOR ', ') AS genres
                FROM Saves s
                JOIN Album a ON s.album_id = a.album_id
                JOIN ReleaseType rt ON a.type_id = rt.type_id
                LEFT JOIN RecordLabel rl ON a.label_id = rl.label_id
                LEFT JOIN Rates r ON a.album_id = r.album_id
                LEFT JOIN CreditedOn co ON a.album_id = co.album_id
                LEFT JOIN Artist ar ON co.artist_id = ar.artist_id
                LEFT JOIN AlbumGenre ag ON a.album_id = ag.album_id
                LEFT JOIN Genre g ON ag.genre_id = g.genre_id
                WHERE s.user_id = ?
                """);

        if (statusFilter != null && !statusFilter.isBlank()) {
            validateStatus(statusFilter);
            sql.append(" AND s.status = ?");
        }

        sql.append("""
                GROUP BY s.status, a.album_id, a.title, a.release_date, rl.name, rt.name
                ORDER BY FIELD(s.status, 'Favorite', 'Owned', 'Wishlist'), a.title
                """);

        if (statusFilter != null && !statusFilter.isBlank()) {
            return jdbcTemplate.query(
                    sql.toString(),
                    (rs, rowNum) -> mapSavedAlbum(rs),
                    userId,
                    statusFilter);
        }
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> mapSavedAlbum(rs), userId);
    }

    private static SavedAlbumDto mapSavedAlbum(ResultSet rs) throws SQLException {
        java.sql.Date date = rs.getDate("release_date");
        LocalDate releaseDate = date != null ? date.toLocalDate() : null;
        double avg = rs.getDouble("avg_rating");
        Double avgRating = rs.wasNull() ? null : avg;
        return new SavedAlbumDto(
                rs.getString("status"),
                new AlbumSummaryDto(
                        rs.getInt("album_id"),
                        rs.getString("title"),
                        releaseDate,
                        rs.getString("record_label"),
                        rs.getString("release_type"),
                        avgRating,
                        rs.getLong("rating_count"),
                        rs.getString("artists"),
                        rs.getString("genres")));
    }

    private static void validateStatus(String status) {
        if (!STATUSES.contains(status)) {
            throw new IllegalArgumentException("Invalid save status: " + status);
        }
    }

    public record SavedAlbumDto(String status, AlbumSummaryDto album) {
    }
}
