package com.musicvault.service;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final JdbcTemplate jdbcTemplate;

    public AdminService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isAdministrator(Integer userId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM Administrator WHERE user_id = ?",
                Integer.class,
                userId);
        return count != null && count > 0;
    }


    public List<IdNameDto> listGenres() {
        return listIdName("Genre", "genre_id", "name");
    }

    public void createGenre(String name) {
        jdbcTemplate.update("INSERT INTO Genre (name) VALUES (?)", name.trim());
    }

    public void updateGenre(Integer id, String name) {
        jdbcTemplate.update("UPDATE Genre SET name = ? WHERE genre_id = ?", name.trim(), id);
    }

    public void deleteGenre(Integer id) {
        jdbcTemplate.update("DELETE FROM Genre WHERE genre_id = ?", id);
    }

    public List<IdNameDto> listLabels() {
        return listIdName("RecordLabel", "label_id", "name");
    }

    public void createLabel(String name) {
        jdbcTemplate.update("INSERT INTO RecordLabel (name) VALUES (?)", name.trim());
    }

    public void updateLabel(Integer id, String name) {
        jdbcTemplate.update("UPDATE RecordLabel SET name = ? WHERE label_id = ?", name.trim(), id);
    }

    public void deleteLabel(Integer id) {
        jdbcTemplate.update("DELETE FROM RecordLabel WHERE label_id = ?", id);
    }

    public List<IdNameDto> listReleaseTypes() {
        return listIdName("ReleaseType", "type_id", "name");
    }

    public void createReleaseType(String name) {
        jdbcTemplate.update("INSERT INTO ReleaseType (name) VALUES (?)", name.trim());
    }

    public void updateReleaseType(Integer id, String name) {
        jdbcTemplate.update("UPDATE ReleaseType SET name = ? WHERE type_id = ?", name.trim(), id);
    }

    public void deleteReleaseType(Integer id) {
        jdbcTemplate.update("DELETE FROM ReleaseType WHERE type_id = ?", id);
    }

    public List<IdNameDto> listStreamingServices() {
        return listIdName("StreamingService", "service_id", "name");
    }

    public void createStreamingService(String name) {
        jdbcTemplate.update("INSERT INTO StreamingService (name) VALUES (?)", name.trim());
    }

    public void updateStreamingService(Integer id, String name) {
        jdbcTemplate.update("UPDATE StreamingService SET name = ? WHERE service_id = ?", name.trim(), id);
    }

    public void deleteStreamingService(Integer id) {
        jdbcTemplate.update("DELETE FROM StreamingService WHERE service_id = ?", id);
    }

    public List<IdNameDto> listArtists() {
        return listIdName("Artist", "artist_id", "name");
    }

    public void createArtist(String name) {
        jdbcTemplate.update("INSERT INTO Artist (name) VALUES (?)", name.trim());
    }

    public void updateArtist(Integer id, String name) {
        jdbcTemplate.update("UPDATE Artist SET name = ? WHERE artist_id = ?", name.trim(), id);
    }

    public void deleteArtist(Integer id) {
        jdbcTemplate.update("DELETE FROM Artist WHERE artist_id = ?", id);
    }


    public List<AdminAlbumDto> listAlbums() {
        return jdbcTemplate.query(
                """
                        SELECT a.album_id, a.title, a.release_date, a.label_id, a.type_id,
                               rl.name AS label_name, rt.name AS type_name
                        FROM Album a
                        JOIN ReleaseType rt ON a.type_id = rt.type_id
                        LEFT JOIN RecordLabel rl ON a.label_id = rl.label_id
                        ORDER BY a.title
                        """,
                (rs, rowNum) -> new AdminAlbumDto(
                        rs.getInt("album_id"),
                        rs.getString("title"),
                        rs.getDate("release_date") != null ? rs.getDate("release_date").toLocalDate() : null,
                        (Integer) rs.getObject("label_id"),
                        rs.getInt("type_id"),
                        rs.getString("label_name"),
                        rs.getString("type_name")));
    }

    public Optional<AdminAlbumDto> getAlbum(Integer albumId) {
        List<AdminAlbumDto> albums = jdbcTemplate.query(
                """
                        SELECT a.album_id, a.title, a.release_date, a.label_id, a.type_id,
                               rl.name AS label_name, rt.name AS type_name
                        FROM Album a
                        JOIN ReleaseType rt ON a.type_id = rt.type_id
                        LEFT JOIN RecordLabel rl ON a.label_id = rl.label_id
                        WHERE a.album_id = ?
                        """,
                (rs, rowNum) -> new AdminAlbumDto(
                        rs.getInt("album_id"),
                        rs.getString("title"),
                        rs.getDate("release_date") != null ? rs.getDate("release_date").toLocalDate() : null,
                        (Integer) rs.getObject("label_id"),
                        rs.getInt("type_id"),
                        rs.getString("label_name"),
                        rs.getString("type_name")),
                albumId);
        return albums.stream().findFirst();
    }

    public Integer createAlbum(String title, LocalDate releaseDate, Integer labelId, Integer typeId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    """
                            INSERT INTO Album (title, release_date, cover_art, label_id, type_id)
                            VALUES (?, ?, NULL, ?, ?)
                            """,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title.trim());
            if (releaseDate != null) {
                ps.setDate(2, Date.valueOf(releaseDate));
            } else {
                ps.setDate(2, null);
            }
            if (labelId != null) {
                ps.setInt(3, labelId);
            } else {
                ps.setObject(3, null);
            }
            ps.setInt(4, typeId);
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to create album");
        }
        return key.intValue();
    }

    public void updateAlbum(Integer albumId, String title, LocalDate releaseDate, Integer labelId, Integer typeId) {
        jdbcTemplate.update(
                """
                        UPDATE Album
                        SET title = ?, release_date = ?, label_id = ?, type_id = ?
                        WHERE album_id = ?
                        """,
                title.trim(),
                releaseDate != null ? Date.valueOf(releaseDate) : null,
                labelId,
                typeId,
                albumId);
    }

    public void deleteAlbum(Integer albumId) {
        jdbcTemplate.update("DELETE FROM Album WHERE album_id = ?", albumId);
    }

    public List<AdminTrackDto> listTracks(Integer albumId) {
        return jdbcTemplate.query(
                """
                        SELECT track_no, title, duration
                        FROM Track
                        WHERE album_id = ?
                        ORDER BY track_no
                        """,
                (rs, rowNum) -> new AdminTrackDto(
                        rs.getInt("track_no"),
                        rs.getString("title"),
                        rs.getTime("duration") != null ? rs.getTime("duration").toLocalTime() : null),
                albumId);
    }

    public void addTrack(Integer albumId, Integer trackNo, String title, LocalTime duration) {
        jdbcTemplate.update(
                """
                        INSERT INTO Track (album_id, track_no, title, duration)
                        VALUES (?, ?, ?, ?)
                        """,
                albumId,
                trackNo,
                title.trim(),
                duration != null ? Time.valueOf(duration) : null);
    }

    public void deleteTrack(Integer albumId, Integer trackNo) {
        jdbcTemplate.update(
                "DELETE FROM Track WHERE album_id = ? AND track_no = ?",
                albumId,
                trackNo);
    }

    public List<IdNameDto> listAlbumGenres(Integer albumId) {
        return jdbcTemplate.query(
                """
                        SELECT g.genre_id AS id, g.name
                        FROM AlbumGenre ag
                        JOIN Genre g ON ag.genre_id = g.genre_id
                        WHERE ag.album_id = ?
                        ORDER BY g.name
                        """,
                (rs, rowNum) -> new IdNameDto(rs.getInt("id"), rs.getString("name")),
                albumId);
    }

    public void addAlbumGenre(Integer albumId, Integer genreId) {
        jdbcTemplate.update(
                "INSERT IGNORE INTO AlbumGenre (album_id, genre_id) VALUES (?, ?)",
                albumId,
                genreId);
    }

    public void removeAlbumGenre(Integer albumId, Integer genreId) {
        jdbcTemplate.update(
                "DELETE FROM AlbumGenre WHERE album_id = ? AND genre_id = ?",
                albumId,
                genreId);
    }


    public List<AdminReviewDto> listReviews() {
        return jdbcTemplate.query(
                """
                        SELECT r.review_id, r.content, r.post_date, r.moderated_by,
                               u.username AS author, a.title AS album_title, a.album_id
                        FROM Review r
                        JOIN User u ON r.author_id = u.user_id
                        JOIN Album a ON r.album_id = a.album_id
                        ORDER BY r.post_date DESC, r.review_id DESC
                        """,
                (rs, rowNum) -> new AdminReviewDto(
                        rs.getInt("review_id"),
                        rs.getString("content"),
                        rs.getDate("post_date").toLocalDate(),
                        rs.getString("author"),
                        rs.getInt("album_id"),
                        rs.getString("album_title"),
                        (Integer) rs.getObject("moderated_by")));
    }

    public void moderateAndDeleteReview(Integer reviewId, Integer adminUserId) {
        jdbcTemplate.update(
                "UPDATE Review SET moderated_by = ? WHERE review_id = ?",
                adminUserId,
                reviewId);
        jdbcTemplate.update("DELETE FROM Review WHERE review_id = ?", reviewId);
    }

    private List<IdNameDto> listIdName(String table, String idColumn, String nameColumn) {
        return jdbcTemplate.query(
                "SELECT " + idColumn + " AS id, " + nameColumn + " AS name FROM " + table + " ORDER BY " + nameColumn,
                (rs, rowNum) -> new IdNameDto(rs.getInt("id"), rs.getString("name")));
    }

    public record IdNameDto(Integer id, String name) {
    }

    public record AdminAlbumDto(
            Integer albumId,
            String title,
            LocalDate releaseDate,
            Integer labelId,
            Integer typeId,
            String labelName,
            String typeName) {
    }

    public record AdminTrackDto(Integer trackNo, String title, LocalTime duration) {
    }

    public record AdminReviewDto(
            Integer reviewId,
            String content,
            LocalDate postDate,
            String authorUsername,
            Integer albumId,
            String albumTitle,
            Integer moderatedBy) {
    }
}
