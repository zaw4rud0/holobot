package dev.zawarudo.holo.modules.akinator;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class AkinatorSessionManager {

    private final Map<Long, AkinatorSession> byUser = new ConcurrentHashMap<>();

    public Optional<AkinatorSession> getSession(long userId) {
        return Optional.ofNullable(byUser.get(userId));
    }

    public boolean hasActiveSession(long userId) {
        AkinatorSession s = byUser.get(userId);
        return s != null && !s.isFinished();
    }

    public boolean registerSession(AkinatorSession session) {
        long userId = session.userId();
        return byUser.compute(userId, (k, existing) -> {
            if (existing != null && !existing.isFinished()) return existing;
            return session;
        }) == session;
    }

    public void removeSession(long userId) {
        byUser.remove(userId);
    }
}