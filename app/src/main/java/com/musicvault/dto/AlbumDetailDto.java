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
    private Double currentUserRating;
    private ReviewDto editableReview;
    private final List<String> artists = new ArrayList<>();
    private final List<String> genres = new ArrayList<>();
    private final List<TrackDto> tracks = new ArrayList<>();
    private final List<StreamingLinkDto> streamingLinks = new ArrayList<>();
    private final List<ReviewDto> reviews = new ArrayList<>();

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

    public Double getCurrentUserRating() {
        return currentUserRating;
    }

    public void setCurrentUserRating(Double currentUserRating) {
        this.currentUserRating = currentUserRating;
    }

    public ReviewDto getEditableReview() {
        return editableReview;
    }

    public void setEditableReview(ReviewDto editableReview) {
        this.editableReview = editableReview;
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

    public List<ReviewDto> getReviews() {
        return reviews;
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

    public static class ReviewDto {
        private final Integer reviewId;
        private final String content;
        private final LocalDate postDate;
        private final String authorUsername;
        private final boolean ownReview;

        public ReviewDto(
                Integer reviewId,
                String content,
                LocalDate postDate,
                String authorUsername,
                boolean ownReview) {
            this.reviewId = reviewId;
            this.content = content;
            this.postDate = postDate;
            this.authorUsername = authorUsername;
            this.ownReview = ownReview;
        }

        public Integer getReviewId() {
            return reviewId;
        }

        public String getContent() {
            return content;
        }

        public LocalDate getPostDate() {
            return postDate;
        }

        public String getAuthorUsername() {
            return authorUsername;
        }

        public boolean isOwnReview() {
            return ownReview;
        }
    }
}
