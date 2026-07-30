package com.musicvault.service;

import com.musicvault.dto.AlbumDetailDto;
import com.musicvault.dto.AlbumSummaryDto;
import com.musicvault.model.Album;
import com.musicvault.repository.AlbumRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
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
                params.toArray(),
                (rs, rowNum) -> new AlbumSummaryDto(
                        rs.getInt("album_id"),
                        rs.getString("title"),
                        getLocalDate(rs, "release_date"),
                        rs.getString("record_label"),
                        rs.getString("release_type"),
                        getDouble(rs, "avg_rating"),
                        rs.getLong("rating_count"),
                        rs.getString("artists"),
                        rs.getString("genres")));
    }

    public Optional<AlbumDetailDto> getAlbumDetail(Integer albumId) {
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
        jdbcTemplate.query(
                """
                        SELECT ss.name, ao.external_url
                        FROM AvailableOn ao
                        JOIN StreamingService ss ON ao.service_id = ss.service_id
                        WHERE ao.album_id = ?
                        ORDER BY ss.name
                        """,
                rs -> {
                    while (rs.next()) {
                        String service = rs.getString("name");
                        if (seenServices.add(service)) {
                            detail.getStreamingLinks().add(new AlbumDetailDto.StreamingLinkDto(
                                    service,
                                    rs.getString("external_url")));
                        }
                    }
                },
                albumId);

        return Optional.of(detail);
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
