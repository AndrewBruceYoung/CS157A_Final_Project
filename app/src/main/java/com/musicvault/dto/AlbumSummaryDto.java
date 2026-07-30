package com.musicvault.dto;

import java.time.LocalDate;

public class AlbumSummaryDto {

    private final Integer albumId;
    private final String title;
    private final LocalDate releaseDate;
    private final String recordLabel;
    private final String releaseType;
    private final Double avgRating;
    private final Long ratingCount;
    private final String artists;
    private final String genres;

    public AlbumSummaryDto(
            Integer albumId,
            String title,
            LocalDate releaseDate,
            String recordLabel,
            String releaseType,
            Double avgRating,
            Long ratingCount,
            String artists,
            String genres) {
        this.albumId = albumId;
        this.title = title;
        this.releaseDate = releaseDate;
        this.recordLabel = recordLabel;
        this.releaseType = releaseType;
        this.avgRating = avgRating;
        this.ratingCount = ratingCount;
        this.artists = artists;
        this.genres = genres;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public String getRecordLabel() {
        return recordLabel;
    }

    public String getReleaseType() {
        return releaseType;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public Long getRatingCount() {
        return ratingCount;
    }

    public String getArtists() {
        return artists;
    }

    public String getGenres() {
        return genres;
    }
}
