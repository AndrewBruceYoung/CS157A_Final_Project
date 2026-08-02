USE musicvault;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE CreditedOn;
TRUNCATE TABLE AvailableOn;
TRUNCATE TABLE AlbumGenre;
TRUNCATE TABLE ListAlbum;
TRUNCATE TABLE Saves;
TRUNCATE TABLE Rates;
TRUNCATE TABLE Follows;
TRUNCATE TABLE AlbumList;
TRUNCATE TABLE Review;
TRUNCATE TABLE Track;
TRUNCATE TABLE Album;
TRUNCATE TABLE Artist;
TRUNCATE TABLE ArtistRole;
TRUNCATE TABLE StreamingService;
TRUNCATE TABLE Genre;
TRUNCATE TABLE ReleaseType;
TRUNCATE TABLE RecordLabel;
TRUNCATE TABLE Administrator;
TRUNCATE TABLE RegisteredUser;
TRUNCATE TABLE User;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO ReleaseType (type_id, name) VALUES
(1, 'Album'), (2, 'EP'), (3, 'Single'), (4, 'Compilation'), (5, 'Live Album');

INSERT INTO Genre (genre_id, name) VALUES
(1, 'Rock'), (2, 'Pop'), (3, 'Hip-Hop'), (4, 'Jazz'), (5, 'Electronic'), (6, 'R&B'), (7, 'Indie');

INSERT INTO StreamingService (service_id, name) VALUES
(1, 'Spotify'), (2, 'Apple Music'), (3, 'YouTube Music'), (4, 'Tidal');

INSERT INTO ArtistRole (role_name) VALUES
('Primary Artist'), ('Featured Artist'), ('Producer'), ('Composer');

INSERT INTO RecordLabel (label_id, name) VALUES
(1, 'Capitol Records'), (2, 'Columbia Records'), (3, 'XL Recordings'), (4, 'Warp Records'), (5, 'Independent');

INSERT INTO Artist (artist_id, name) VALUES
(1, 'The Beatles'), (2, 'Radiohead'), (3, 'Kendrick Lamar'), (4, 'Billie Eilish'),
(5, 'Daft Punk'), (6, 'Miles Davis'), (7, 'Taylor Swift');

INSERT INTO User (user_id, username, email, password_hash, join_date) VALUES
(1, 'alice', 'alice@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '2024-01-15'),
(2, 'bob', 'bob@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '2024-02-20'),
(3, 'carol', 'carol@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '2024-03-10'),
(4, 'admin_mod', 'admin@musicvault.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '2023-06-01');

INSERT INTO RegisteredUser (user_id, bio, profile_picture) VALUES
(1, 'Classic rock enthusiast and vinyl collector.', NULL),
(2, 'Hip-hop head. Always looking for new releases.', NULL),
(3, 'Indie and electronic listener.', NULL);

INSERT INTO Administrator (user_id, permission_level) VALUES
(4, 'SuperAdmin');

INSERT INTO Album (album_id, title, release_date, cover_art, label_id, type_id) VALUES
(1, 'Abbey Road', '1969-09-26', NULL, 1, 1),
(2, 'OK Computer', '1997-06-16', NULL, 3, 1),
(3, 'To Pimp a Butterfly', '2015-03-15', NULL, 2, 1),
(4, 'When We All Fall Asleep, Where Do We Go?', '2019-03-29', NULL, 5, 1),
(5, 'Random Access Memories', '2013-05-17', NULL, 1, 1),
(6, 'Kind of Blue', '1959-08-17', NULL, 2, 1),
(7, 'Midnights', '2022-10-21', NULL, 1, 1),
(8, 'Kid A', '2000-10-02', NULL, 3, 1);

INSERT INTO Track (album_id, track_no, title, duration) VALUES
(1, 1, 'Come Together', '00:04:20'), (1, 2, 'Something', '00:03:03'), (1, 3, 'Here Comes the Sun', '00:03:05'),
(2, 1, 'Airbag', '00:04:44'), (2, 2, 'Paranoid Android', '00:06:23'), (2, 3, 'Karma Police', '00:04:21'),
(3, 1, 'Wesley''s Theory', '00:04:47'), (3, 2, 'King Kunta', '00:03:54'),
(4, 1, 'bad guy', '00:03:14'), (4, 2, 'when the party''s over', '00:03:16'),
(5, 1, 'Give Life Back to Music', '00:04:34'), (5, 2, 'Get Lucky', '00:06:09'),
(6, 1, 'So What', '00:09:22'), (6, 2, 'Freddie Freeloader', '00:09:46'),
(7, 1, 'Lavender Haze', '00:03:22'), (7, 2, 'Anti-Hero', '00:03:20'),
(8, 1, 'Everything In Its Right Place', '00:04:11'), (8, 2, 'Idioteque', '00:05:09');

INSERT INTO AlbumGenre (album_id, genre_id) VALUES
(1, 1), (2, 1), (2, 7), (3, 3), (3, 6), (4, 2), (4, 5), (5, 5), (5, 2),
(6, 4), (7, 2), (7, 6), (8, 1), (8, 5);

INSERT INTO CreditedOn (album_id, artist_id, role_name) VALUES
(1, 1, 'Primary Artist'), (2, 2, 'Primary Artist'), (2, 2, 'Producer'), (3, 3, 'Primary Artist'),
(4, 4, 'Primary Artist'), (5, 5, 'Primary Artist'), (5, 5, 'Producer'), (6, 6, 'Primary Artist'),
(6, 6, 'Composer'), (7, 7, 'Primary Artist'), (8, 2, 'Primary Artist'), (8, 2, 'Producer');

INSERT INTO AvailableOn (album_id, service_id, external_url) VALUES
(1, 1, 'https://open.spotify.com/album/abbey-road'), (1, 2, 'https://music.apple.com/album/abbey-road'),
(2, 1, 'https://open.spotify.com/album/ok-computer'), (3, 1, 'https://open.spotify.com/album/to-pimp-a-butterfly'),
(3, 3, 'https://music.youtube.com/album/tpab'), (4, 1, 'https://open.spotify.com/album/wwafawdwg'),
(5, 1, 'https://open.spotify.com/album/ram'), (5, 4, 'https://tidal.com/album/ram'),
(7, 1, 'https://open.spotify.com/album/midnights'), (7, 2, 'https://music.apple.com/album/midnights');

INSERT INTO Rates (user_id, album_id, score, rated_at) VALUES
(1, 1, 5.0, '2024-04-01 10:00:00'), (1, 2, 4.5, '2024-04-02 11:30:00'), (1, 6, 5.0, '2024-04-03 09:15:00'),
(2, 3, 5.0, '2024-05-10 14:00:00'), (2, 7, 3.5, '2024-05-12 12:00:00'),
(3, 2, 4.0, '2024-06-01 18:00:00'), (3, 4, 4.5, '2024-06-02 19:00:00'),
(3, 5, 5.0, '2024-06-03 20:00:00'), (3, 8, 4.5, '2024-06-04 21:00:00');

INSERT INTO Review (review_id, content, post_date, author_id, album_id, moderated_by) VALUES
(1, 'A flawless closing chapter for the Beatles. Every track earns its place.', '2024-04-05', 1, 1, NULL),
(2, 'OK Computer still feels ahead of its time. Paranoid Android is a masterpiece.', '2024-06-05', 3, 2, NULL),
(3, 'An ambitious, layered hip-hop album with incredible production.', '2024-05-15', 2, 3, 4);

INSERT INTO Saves (user_id, album_id, status) VALUES
(1, 1, 'Owned'), (1, 1, 'Favorite'), (1, 6, 'Owned'), (2, 3, 'Favorite'),
(2, 7, 'Wishlist'), (3, 5, 'Owned'), (3, 8, 'Favorite');

INSERT INTO AlbumList (list_id, name, description, user_id) VALUES
(1, 'All-Time Classics', 'Albums I keep coming back to.', 1),
(2, '2020s Favorites', 'Recent releases worth replaying.', 3);

INSERT INTO ListAlbum (list_id, album_id, position) VALUES
(1, 1, 1), (1, 6, 2), (1, 2, 3), (2, 4, 1), (2, 7, 2), (2, 5, 3);

INSERT INTO Follows (follower_id, followee_id, since) VALUES
(1, 2, '2024-04-10'), (1, 3, '2024-04-12'), (2, 1, '2024-05-01'), (3, 1, '2024-06-01');
