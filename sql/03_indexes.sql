-- =====================================================================
-- MusicVault - Indexes
-- CS 157A Final Project
--
-- Run order: 01_schema.sql -> 02_seed.sql -> 03_indexes.sql -> 04_queries.sql
-- (Indexes are added after the data loads; bulk-inserting first and
--  indexing after is the normal, slightly faster order.)
--
-- Already indexed automatically, so intentionally NOT repeated here:
--   * Primary keys        - every table (InnoDB clustered index).
--   * UNIQUE columns       - User.username, User.email, Genre.name,
--                            ReleaseType.name, StreamingService.name.
--                            (So "search by username / genre" is already
--                            covered -- an explicit index would just
--                            duplicate the UNIQUE index.)
--   * Foreign key columns  - InnoDB auto-indexes every FK column
--                            (Album.label_id, Album.type_id,
--                            Rates.user_id/album_id, Review.album_id/
--                            author_id, AlbumGenre.genre_id,
--                            CreditedOn.artist_id, ListAlbum.album_id, ...).
--                            Adding explicit indexes on these would be
--                            redundant and only cost write speed + storage.
--
-- The six below cover the remaining search columns and the specific
-- query patterns the application runs.
-- =====================================================================

USE musicvault;

-- --- Frequently-searched, non-unique text columns ---

-- Search albums by title.
CREATE INDEX idx_album_title ON Album(title);

-- Search artists by name.
CREATE INDEX idx_artist_name ON Artist(name);

-- Search by record label name.
CREATE INDEX idx_record_label_name ON RecordLabel(name);

-- --- Composite: browse/sort albums by release date, then title ---
-- Serves ORDER BY release_date [, title]. The leading release_date column
-- also covers plain release-year range queries via its prefix, so a
-- separate single-column release_date index is unnecessary.
-- (Filter by year with a date RANGE, e.g.
--    WHERE release_date BETWEEN '2019-01-01' AND '2019-12-31'
--  rather than YEAR(release_date) = 2019, which cannot use the index.)
CREATE INDEX idx_album_release_title ON Album(release_date, title);

-- --- Covering index: community average rating ---
-- Serves the AVG(score) GROUP BY album_id aggregate used to display each
-- album's average, and the correlated per-album AVG subquery. Because
-- album_id and score are both in the index, MySQL answers these from the
-- index alone without reading table rows. The leading album_id column also
-- satisfies InnoDB's foreign-key index requirement on Rates.album_id, so
-- this does double duty rather than adding redundancy.
CREATE INDEX idx_rates_album_score ON Rates(album_id, score);

-- --- Composite: an album's reviews, newest first ---
-- Serves the album detail page's review feed:
--   SELECT ... FROM Review WHERE album_id = ? ORDER BY post_date DESC
-- Leading album_id also satisfies the FK index on Review.album_id; the
-- trailing post_date lets the sort be read straight from the index.
CREATE INDEX idx_review_album_date ON Review(album_id, post_date);

-- Verify: lists every index on Album (explicit ones + PRIMARY + FK indexes).
SHOW INDEX FROM Album;
