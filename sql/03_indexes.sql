USE musicvault;

CREATE INDEX idx_album_title ON Album(title);
CREATE INDEX idx_artist_name ON Artist(name);
CREATE INDEX idx_record_label_name ON RecordLabel(name);
CREATE INDEX idx_album_release_title ON Album(release_date, title);
CREATE INDEX idx_rates_album_score ON Rates(album_id, score);
CREATE INDEX idx_review_album_date ON Review(album_id, post_date);

SHOW INDEX FROM Album;
