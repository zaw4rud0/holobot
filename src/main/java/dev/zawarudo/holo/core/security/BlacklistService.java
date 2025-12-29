package dev.zawarudo.holo.core.security;

import dev.zawarudo.holo.database.dao.BlacklistedDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BlacklistService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlacklistService.class);

    private final BlacklistedDao dao;
    private final Set<Long> ids = ConcurrentHashMap.newKeySet();

    public BlacklistService(BlacklistedDao dao) {
        this.dao = dao;
        try {
            ids.addAll(dao.findAllUserIds());
        } catch (SQLException e) {
            LOGGER.error("Failed to load blacklist", e);
        }
    }

    public boolean isBlacklisted(long userId) {
        return ids.contains(userId);
    }

    public void blacklist(long userId, String reason, String date) throws SQLException {
        dao.insertIgnore(new BlacklistedDao.Blacklisted(userId, reason, date));
        ids.add(userId);
    }

    public void unblacklist(long userId) throws SQLException {
        dao.deleteByUserId(userId);
        ids.remove(userId);
    }
}