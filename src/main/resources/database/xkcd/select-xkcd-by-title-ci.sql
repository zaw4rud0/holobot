-- Case insensitive
SELECT *
FROM XkcdComics
WHERE lower(title) = lower(?)
LIMIT 1;