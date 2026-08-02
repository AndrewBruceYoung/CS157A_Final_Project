USE musicvault;

SELECT
    a.album_id, a.title, a.release_date, rl.name AS record_label, rt.name AS release_type,
    ROUND(AVG(r.score), 1) AS avg_rating, COUNT(r.user_id) AS rating_count
FROM Album a
JOIN ReleaseType rt ON a.type_id = rt.type_id
LEFT JOIN RecordLabel rl ON a.label_id = rl.label_id
LEFT JOIN Rates r ON a.album_id = r.album_id
GROUP BY a.album_id, a.title, a.release_date, rl.name, rt.name
ORDER BY avg_rating DESC, a.title;

SELECT DISTINCT
    a.album_id, a.title, a.release_date, ar.name AS artist_name, g.name AS genre_name, rl.name AS record_label
FROM Album a
LEFT JOIN CreditedOn co ON a.album_id = co.album_id
LEFT JOIN Artist ar ON co.artist_id = ar.artist_id
LEFT JOIN AlbumGenre ag ON a.album_id = ag.album_id
LEFT JOIN Genre g ON ag.genre_id = g.genre_id
LEFT JOIN RecordLabel rl ON a.label_id = rl.label_id
WHERE a.title LIKE '%Blue%'
   OR ar.name LIKE '%Radiohead%'
   OR g.name = 'Jazz'
   OR YEAR(a.release_date) = 2019
   OR rl.name LIKE '%Capitol%'
ORDER BY a.release_date DESC;

SELECT
    a.album_id, a.title, ROUND(AVG(r.score), 1) AS avg_rating, COUNT(r.user_id) AS rating_count
FROM Album a
JOIN Rates r ON a.album_id = r.album_id
GROUP BY a.album_id, a.title
HAVING COUNT(r.user_id) >= 1
ORDER BY avg_rating DESC, rating_count DESC
LIMIT 5;

SELECT
    a.title AS album_title, t.track_no, t.title AS track_title, t.duration,
    g.name AS genre, ar.name AS artist, co.role_name, ss.name AS streaming_service, ao.external_url
FROM Album a
JOIN Track t ON a.album_id = t.album_id
LEFT JOIN AlbumGenre ag ON a.album_id = ag.album_id
LEFT JOIN Genre g ON ag.genre_id = g.genre_id
LEFT JOIN CreditedOn co ON a.album_id = co.album_id
LEFT JOIN Artist ar ON co.artist_id = ar.artist_id
LEFT JOIN AvailableOn ao ON a.album_id = ao.album_id
LEFT JOIN StreamingService ss ON ao.service_id = ss.service_id
WHERE a.album_id = 2
ORDER BY t.track_no, g.name, ar.name;

SELECT
    u.username, a.title AS album_title, r.score AS user_score,
    (SELECT ROUND(AVG(r2.score), 1) FROM Rates r2 WHERE r2.album_id = a.album_id) AS album_avg
FROM Rates r
JOIN RegisteredUser ru ON r.user_id = ru.user_id
JOIN User u ON ru.user_id = u.user_id
JOIN Album a ON r.album_id = a.album_id
WHERE r.score > (SELECT AVG(r3.score) FROM Rates r3 WHERE r3.album_id = a.album_id)
ORDER BY a.title, r.score DESC;

SELECT
    u.username, COUNT(DISTINCT re.review_id) AS review_count, ROUND(AVG(rt.score), 1) AS avg_score_given
FROM RegisteredUser ru
JOIN User u ON ru.user_id = u.user_id
LEFT JOIN Review re ON ru.user_id = re.author_id
LEFT JOIN Rates rt ON ru.user_id = rt.user_id
GROUP BY u.user_id, u.username
ORDER BY review_count DESC, avg_score_given DESC;

SELECT u.username, al.name AS list_name, la.position, a.title AS album_title
FROM AlbumList al
JOIN RegisteredUser ru ON al.user_id = ru.user_id
JOIN User u ON ru.user_id = u.user_id
JOIN ListAlbum la ON al.list_id = la.list_id
JOIN Album a ON la.album_id = a.album_id
WHERE u.username = 'alice'
ORDER BY al.name, la.position;

SELECT u.username, a.title, s.status, ss.name AS streaming_service
FROM Saves s
JOIN RegisteredUser ru ON s.user_id = ru.user_id
JOIN User u ON ru.user_id = u.user_id
JOIN Album a ON s.album_id = a.album_id
JOIN AvailableOn ao ON a.album_id = ao.album_id
JOIN StreamingService ss ON ao.service_id = ss.service_id
WHERE s.status = 'Favorite' AND ss.name = 'Spotify'
ORDER BY u.username, a.title;
