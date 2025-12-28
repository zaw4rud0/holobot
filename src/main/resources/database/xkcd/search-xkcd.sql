SELECT c.*
FROM XkcdComics_fts f
         JOIN XkcdComics c ON c.id = f.rowid
WHERE XkcdComics_fts MATCH ?
ORDER BY bm25(XkcdComics_fts)
LIMIT ? OFFSET ?;