package com.musicvault.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AlbumDetailDto {

    private Integer albumId;
    private String title;
    private LocalDate releaseDate;
    private String recordLabel;
    private String releaseType;
    private Double avgRating;
    private Long ratingCount;
    private final List<String> artists = new ArrayList<>();
    private final List<String> genres = new ArrayList<>();
    private final List<TrackDto> tracks = new ArrayList<>();
    private final List<StreamingLinkDto> streamingLinks = new ArrayList<>();

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getRecordLabel() {
        return recordLabel;
    }

    public void setRecordLabel(String recordLabel) {
        this.recordLabel = recordLabel;
    }

    public String getReleaseType() {
        return releaseType;
    }

    public void setReleaseType(String releaseType) {
        this.releaseType = releaseType;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }

    public Long getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Long ratingCount) {
        this.ratingCount = ratingCount;
    }

    public List<String> getArtists() {
        return artists;
    }

    public List<String> getGenres() {
        return genres;
    }

    public List<TrackDto> getTracks() {
        return tracks;
    }

    public List<StreamingLinkDto> getStreamingLinks() {
        return streamingLinks;
    }

    public static class TrackDto {
        private final Integer trackNo;
        private final String title;
        private final LocalTime duration;

        public TrackDto(Integer trackNo, String title, LocalTime duration) {
            this.trackNo = trackNo;
            this.title = title;
            this.duration = duration;
        }

        public Integer getTrackNo() {
            return trackNo;
        }

        public String getTitle() {
            return title;
        }

        public LocalTime getDuration() {
            return duration;
        }
    }

    public static class StreamingLinkDto {
        private final String serviceName;
        private final String externalUrl;

        public StreamingLinkDto(String serviceName, String externalUrl) {
            this.serviceName = serviceName;
            this.externalUrl = externalUrl;
        }

        public String getServiceName() {
            return serviceName;
        }

        public String getExternalUrl() {
            return externalUrl;
        }
    }
}
