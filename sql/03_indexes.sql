USE musicvault;

CREATE INDEX idx_user_username ON User(username);
CREATE INDEX idx_album_title ON Album(title);
CREATE INDEX idx_album_release_date ON Album(release_date);
CREATE INDEX idx_artist_name ON Artist(name);
CREATE INDEX idx_genre_name ON Genre(name);
CREATE INDEX idx_record_label_name ON RecordLabel(name);
CREATE INDEX idx_album_label_id ON Album(label_id);
CREATE INDEX idx_album_type_id ON Album(type_id);
CREATE INDEX idx_rates_album_id ON Rates(album_id);
CREATE INDEX idx_rates_user_id ON Rates(user_id);
CREATE INDEX idx_review_album_id ON Review(album_id);
CREATE INDEX idx_review_author_id ON Review(author_id);
CREATE INDEX idx_album_genre_genre_id ON AlbumGenre(genre_id);
CREATE INDEX idx_credited_on_artist_id ON CreditedOn(artist_id);
CREATE INDEX idx_list_album_album_id ON ListAlbum(album_id);
CREATE INDEX idx_album_release_title ON Album(release_date, title);
