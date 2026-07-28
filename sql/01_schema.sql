CREATE DATABASE IF NOT EXISTS musicvault;
USE musicvault;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS CreditedOn;
DROP TABLE IF EXISTS AvailableOn;
DROP TABLE IF EXISTS AlbumGenre;
DROP TABLE IF EXISTS ListAlbum;
DROP TABLE IF EXISTS Saves;
DROP TABLE IF EXISTS Rates;
DROP TABLE IF EXISTS Follows;
DROP TABLE IF EXISTS AlbumList;
DROP TABLE IF EXISTS Review;
DROP TABLE IF EXISTS Track;
DROP TABLE IF EXISTS Album;
DROP TABLE IF EXISTS ArtistRole;
DROP TABLE IF EXISTS Artist;
DROP TABLE IF EXISTS StreamingService;
DROP TABLE IF EXISTS Genre;
DROP TABLE IF EXISTS ReleaseType;
DROP TABLE IF EXISTS RecordLabel;
DROP TABLE IF EXISTS Administrator;
DROP TABLE IF EXISTS RegisteredUser;
DROP TABLE IF EXISTS User;
SET FOREIGN_KEY_CHECKS = 1;


CREATE TABLE User (
    user_id       INT PRIMARY KEY AUTO_INCREMENT,
    username      VARCHAR(100) NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    join_date     DATE NOT NULL
);

CREATE TABLE RegisteredUser (
    user_id         INT PRIMARY KEY,
    bio             VARCHAR(1000),
    profile_picture VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES User(user_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Administrator (
    user_id          INT PRIMARY KEY,
    permission_level ENUM('Moderator','SuperAdmin') NOT NULL DEFAULT 'Moderator',
    FOREIGN KEY (user_id) REFERENCES User(user_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE RecordLabel (
    label_id INT PRIMARY KEY AUTO_INCREMENT,
    name     VARCHAR(150) NOT NULL
);

CREATE TABLE ReleaseType (
    type_id INT PRIMARY KEY AUTO_INCREMENT,
    name    VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE Genre (
    genre_id INT PRIMARY KEY AUTO_INCREMENT,
    name     VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE StreamingService (
    service_id INT PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE Artist (
    artist_id INT PRIMARY KEY AUTO_INCREMENT,
    name      VARCHAR(200) NOT NULL
);

CREATE TABLE ArtistRole (
    role_name VARCHAR(50) PRIMARY KEY
);


CREATE TABLE Album (
    album_id     INT PRIMARY KEY AUTO_INCREMENT,
    title        VARCHAR(300) NOT NULL,
    release_date DATE,
    cover_art    VARCHAR(255),
    label_id     INT NULL,
    type_id      INT NOT NULL,
    FOREIGN KEY (label_id) REFERENCES RecordLabel(label_id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (type_id) REFERENCES ReleaseType(type_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);


CREATE TABLE Track (
    album_id INT NOT NULL,
    track_no INT NOT NULL,
    title    VARCHAR(300) NOT NULL,
    duration TIME,
    PRIMARY KEY (album_id, track_no),
    FOREIGN KEY (album_id) REFERENCES Album(album_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE Review (
    review_id    INT PRIMARY KEY AUTO_INCREMENT,
    content      VARCHAR(2000) NOT NULL,
    post_date    DATE NOT NULL,
    author_id    INT NOT NULL,
    album_id     INT NOT NULL,
    moderated_by INT NULL,
    FOREIGN KEY (author_id) REFERENCES RegisteredUser(user_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (album_id) REFERENCES Album(album_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (moderated_by) REFERENCES Administrator(user_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);


CREATE TABLE AlbumList (
    list_id     INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    user_id     INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES RegisteredUser(user_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE Follows (
    follower_id INT NOT NULL,
    followee_id INT NOT NULL,
    since       DATE,
    PRIMARY KEY (follower_id, followee_id),
    FOREIGN KEY (follower_id) REFERENCES User(user_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (followee_id) REFERENCES User(user_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TRIGGER IF EXISTS trg_follows_no_self;
DELIMITER $$
CREATE TRIGGER trg_follows_no_self
BEFORE INSERT ON Follows
FOR EACH ROW
BEGIN
    IF NEW.follower_id = NEW.followee_id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'A user cannot follow themselves.';
    END IF;
END$$
DELIMITER ;


CREATE TABLE Rates (
    user_id  INT NOT NULL,
    album_id INT NOT NULL,
    score    DECIMAL(2,1) NOT NULL,
    rated_at DATETIME NOT NULL,
    PRIMARY KEY (user_id, album_id),
    FOREIGN KEY (user_id) REFERENCES RegisteredUser(user_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (album_id) REFERENCES Album(album_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CHECK (score BETWEEN 0.5 AND 5.0 AND score * 2 = FLOOR(score * 2))
);

CREATE TABLE Saves (
    user_id  INT NOT NULL,
    album_id INT NOT NULL,
    status   ENUM('Owned','Favorite','Wishlist') NOT NULL,
    PRIMARY KEY (user_id, album_id, status),
    FOREIGN KEY (user_id) REFERENCES RegisteredUser(user_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (album_id) REFERENCES Album(album_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE ListAlbum (
    list_id  INT NOT NULL,
    album_id INT NOT NULL,
    position INT NOT NULL,
    PRIMARY KEY (list_id, album_id),
    UNIQUE (list_id, position),
    FOREIGN KEY (list_id) REFERENCES AlbumList(list_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (album_id) REFERENCES Album(album_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE AlbumGenre (
    album_id INT NOT NULL,
    genre_id INT NOT NULL,
    PRIMARY KEY (album_id, genre_id),
    FOREIGN KEY (album_id) REFERENCES Album(album_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES Genre(genre_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE AvailableOn (
    album_id     INT NOT NULL,
    service_id   INT NOT NULL,
    external_url VARCHAR(500),
    PRIMARY KEY (album_id, service_id),
    FOREIGN KEY (album_id) REFERENCES Album(album_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (service_id) REFERENCES StreamingService(service_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE CreditedOn (
    album_id  INT NOT NULL,
    artist_id INT NOT NULL,
    role_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (album_id, artist_id, role_name),
    FOREIGN KEY (album_id) REFERENCES Album(album_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (artist_id) REFERENCES Artist(artist_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (role_name) REFERENCES ArtistRole(role_name)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

SHOW TABLES;
