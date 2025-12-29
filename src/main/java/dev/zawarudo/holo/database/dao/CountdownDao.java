package dev.zawarudo.holo.database.dao;

import dev.zawarudo.holo.database.SQLManager;
import dev.zawarudo.holo.modules.countdown.Countdown;

import java.sql.SQLException;
import java.util.List;

public final class CountdownDao {

    private final SQLManager sql;

    public CountdownDao(SQLManager sql) {
        this.sql = sql;
    }

    public List<Countdown> findAll() throws SQLException {
        String stmt = sql.getStatement("select-countdown");

        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Countdown> findAllById(long userId) throws SQLException {
        String stmt = sql.getStatement("select-countdown-by-user");

        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int insertIgnore(Countdown countdown) throws SQLException {
        String stmt = sql.getStatement("insert-countdown");

        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int deleteIgnore(long countdownId) throws SQLException {
        String stmt = sql.getStatement("delete-countdown");

        throw new UnsupportedOperationException("Not supported yet.");
    }
}