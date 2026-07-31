package com.musicvault.service;

import com.musicvault.dto.AlbumDetailDto;
import com.musicvault.dto.AlbumSummaryDto;
import com.musicvault.model.Album;
import com.musicvault.repository.AlbumRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AlbumService {

    private final JdbcTemplate jdbcTemplate;
    private final AlbumRepository albumRepository;

    public AlbumService(JdbcTemplate jdbcTemplate, AlbumRepository albumRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.albumRepository = albumRepository;
    }

    public List<AlbumSummaryDto> browseAlbums(String query) {
        StringBuilder sql = new StringBuilder("""
                SELECT
                    a.album_id,
                    a.title,
                    a.release_date,
                    rl.name AS record_label,
                    rt.name AS release_type,
                    ROUND(AVG(r.score), 1) AS avg_rating,
                    COUNT(r.user_id) AS rating_count,
                    GROUP_CONCAT(DISTINCT ar.name ORDER BY ar.name SEPARATOR ', ') AS artists,
                    GROUP_CONCAT(DISTINCT g.name ORDER BY g.name SEPARATOR ', ') AS genres
                FROM Album a
                JOIN ReleaseType rt ON a.type_id = rt.type_id
                LEFT JOIN RecordLabel rl ON a.label_id = rl.label_id
                LEFT JOIN Rates r ON a.album_id = r.album_id
                LEFT JOIN CreditedOn co ON a.album_id = co.album_id
                LEFT JOIN Artist ar ON co.artist_id = ar.artist_id
                LEFT JOIN AlbumGenre ag ON a.album_id = ag.album_id
                LEFT JOIN Genre g ON ag.genre_id = g.genre_id
                """);

        List<Object> params = new ArrayList<>();
        if (query != null && !query.isBlank()) {
            sql.append("""
                    WHERE a.title LIKE ?
                       OR ar.name LIKE ?
                       OR g.name LIKE ?
                       OR rl.name LIKE ?
                       OR CAST(YEAR(a.release_date) AS CHAR) = ?
                    """);
            String like = "%" + query.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(query.trim());
        }

        sql.append("""
                GROUP BY a.album_id, a.title, a.release_date, rl.name, rt.name
                ORDER BY avg_rating DESC, a.title
                """);

        return jdbcTemplate.query(
                sql.toString(),
                (rs, rowNum) -> new AlbumSummaryDto(
                        rs.getInt("album_id"),
                        rs.getString("title"),
                        getLocalDate(rs, "release_date"),
                        rs.getString("record_label"),
                        rs.getString("release_type"),
                        getDouble(rs, "avg_rating"),
                        rs.getLong("rating_count"),
                        rs.getString("artists"),
                        rs.getString("genres")),
                params.toArray());
    }

    public Optional<AlbumDetailDto> getAlbumDetail(Integer albumId) {
        return getAlbumDetail(albumId, null, null);
    }

    public Optional<AlbumDetailDto> getAlbumDetail(Integer albumId, Integer currentUserId, Integer editReviewId) {
        Optional<Album> albumOptional = albumRepository.findById(albumId);
        if (albumOptional.isEmpty()) {
            return Optional.empty();
        }

        Album album = albumOptional.get();
        AlbumDetailDto detail = new AlbumDetailDto();
        detail.setAlbumId(album.getAlbumId());
        detail.setTitle(album.getTitle());
        detail.setReleaseDate(album.getReleaseDate());
        detail.setRecordLabel(album.getRecordLabel() != null ? album.getRecordLabel().getName() : null);
        detail.setReleaseType(album.getReleaseType().getName());

        jdbcTemplate.query(
                """
                        SELECT ROUND(AVG(score), 1) AS avg_rating, COUNT(user_id) AS rating_count
                        FROM Rates
                        WHERE album_id = ?
                        """,
                rs -> {
                    if (rs.next()) {
                        detail.setAvgRating(getDouble(rs, "avg_rating"));
                        detail.setRatingCount(rs.getLong("rating_count"));
                    }
                },
                albumId);

        if (currentUserId != null) {
            jdbcTemplate.query(
                    """
                            SELECT score
                            FROM Rates
                            WHERE album_id = ? AND user_id = ?
                            """,
                    rs -> {
                        if (rs.next()) {
                            detail.setCurrentUserRating(getDouble(rs, "score"));
                        }
                    },
                    albumId,
                    currentUserId);

            detail.getCurrentUserSaveStatuses().addAll(jdbcTemplate.query(
                    "SELECT status FROM Saves WHERE user_id = ? AND album_id = ?",
                    (rs, rowNum) -> rs.getString("status"),
                    currentUserId,
                    albumId));

            detail.getCurrentUserLists().addAll(jdbcTemplate.query(
                    """
                            SELECT list_id, name
                            FROM AlbumList
                            WHERE user_id = ?
                            ORDER BY name
                            """,
                    (rs, rowNum) -> new AlbumDetailDto.UserListOptionDto(
                            rs.getInt("list_id"),
                            rs.getString("name")),
                    currentUserId));
        }

        detail.getArtists().addAll(jdbcTemplate.query(
                """
                        SELECT DISTINCT ar.name
                        FROM CreditedOn co
                        JOIN Artist ar ON co.artist_id = ar.artist_id
                        WHERE co.album_id = ?
                        ORDER BY ar.name
                        """,
                (rs, rowNum) -> rs.getString("name"),
                albumId));

        detail.getGenres().addAll(jdbcTemplate.query(
                """
                        SELECT DISTINCT g.name
                        FROM AlbumGenre ag
                        JOIN Genre g ON ag.genre_id = g.genre_id
                        WHERE ag.album_id = ?
                        ORDER BY g.name
                        """,
                (rs, rowNum) -> rs.getString("name"),
                albumId));

        detail.getTracks().addAll(jdbcTemplate.query(
                """
                        SELECT track_no, title, duration
                        FROM Track
                        WHERE album_id = ?
                        ORDER BY track_no
                        """,
                (rs, rowNum) -> new AlbumDetailDto.TrackDto(
                        rs.getInt("track_no"),
                        rs.getString("title"),
                        toLocalTime(rs.getTime("duration"))),
                albumId));

        Set<String> seenServices = new LinkedHashSet<>();
        List<AlbumDetailDto.StreamingLinkDto> streamingLinks = jdbcTemplate.query(
                """
                        SELECT ss.name, ao.external_url
                        FROM AvailableOn ao
                        JOIN StreamingService ss ON ao.service_id = ss.service_id
                        WHERE ao.album_id = ?
                        ORDER BY ss.name
                        """,
                (rs, rowNum) -> new AlbumDetailDto.StreamingLinkDto(
                        rs.getString("name"),
                        rs.getString("external_url")),
                albumId);
        for (AlbumDetailDto.StreamingLinkDto link : streamingLinks) {
            if (seenServices.add(link.getServiceName())) {
                detail.getStreamingLinks().add(link);
            }
        }

        detail.getReviews().addAll(jdbcTemplate.query(
                """
                        SELECT r.review_id, r.content, r.post_date, u.username,
                               CASE WHEN r.author_id = ? THEN 1 ELSE 0 END AS own_review
                        FROM Review r
                        JOIN User u ON r.author_id = u.user_id
                        WHERE r.album_id = ?
                        ORDER BY r.post_date DESC, r.review_id DESC
                        """,
                (rs, rowNum) -> new AlbumDetailDto.ReviewDto(
                        rs.getInt("review_id"),
                        rs.getString("content"),
                        getLocalDate(rs, "post_date"),
                        rs.getString("username"),
                        rs.getInt("own_review") == 1),
                currentUserId == null ? -1 : currentUserId,
                albumId));

        if (currentUserId != null && editReviewId != null) {
            List<AlbumDetailDto.ReviewDto> editableReviews = jdbcTemplate.query(
                    """
                            SELECT review_id, content, post_date
                            FROM Review
                            WHERE review_id = ? AND album_id = ? AND author_id = ?
                            """,
                    (rs, rowNum) -> new AlbumDetailDto.ReviewDto(
                            rs.getInt("review_id"),
                            rs.getString("content"),
                            getLocalDate(rs, "post_date"),
                            null,
                            true),
                    editReviewId,
                    albumId,
                    currentUserId);
            if (!editableReviews.isEmpty()) {
                detail.setEditableReview(editableReviews.get(0));
            }
        }

        return Optional.of(detail);
    }

    public void saveRating(Integer albumId, Integer userId, Double score) {
        jdbcTemplate.update(
                """
                        INSERT INTO Rates (user_id, album_id, score, rated_at)
                        VALUES (?, ?, ?, ?)
                        ON DUPLICATE KEY UPDATE score = VALUES(score), rated_at = VALUES(rated_at)
                        """,
                userId,
                albumId,
                score,
                LocalDateTime.now());
    }

    public void deleteRating(Integer albumId, Integer userId) {
        jdbcTemplate.update(
                "DELETE FROM Rates WHERE album_id = ? AND user_id = ?",
                albumId,
                userId);
    }

    public void createReview(Integer albumId, Integer userId, String content) {
        jdbcTemplate.update(
                """
                        INSERT INTO Review (content, post_date, author_id, album_id, moderated_by)
                        VALUES (?, ?, ?, ?, NULL)
                        """,
                content,
                LocalDate.now(),
                userId,
                albumId);
    }

    public void updateReview(Integer reviewId, Integer albumId, Integer userId, String content) {
        jdbcTemplate.update(
                """
                        UPDATE Review
                        SET content = ?, post_date = ?
                        WHERE review_id = ? AND album_id = ? AND author_id = ?
                        """,
                content,
                LocalDate.now(),
                reviewId,
                albumId,
                userId);
    }

    public void deleteReview(Integer reviewId, Integer albumId, Integer userId) {
        jdbcTemplate.update(
                "DELETE FROM Review WHERE review_id = ? AND album_id = ? AND author_id = ?",
                reviewId,
                albumId,
                userId);
    }

    private static LocalDate getLocalDate(ResultSet rs, String column) throws SQLException {
        java.sql.Date date = rs.getDate(column);
        return date != null ? date.toLocalDate() : null;
    }

    private static Double getDouble(ResultSet rs, String column) throws SQLException {
        double value = rs.getDouble(column);
        return rs.wasNull() ? null : value;
    }

    private static LocalTime toLocalTime(Time time) {
        return time != null ? time.toLocalTime() : null;
    }
}
