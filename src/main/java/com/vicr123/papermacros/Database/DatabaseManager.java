package com.vicr123.papermacros.Database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseManager {
    private Dao<Macro, Long> macrosDao;

    public DatabaseManager() {
        try {
            JdbcPooledConnectionSource connectionSource = new JdbcPooledConnectionSource("jdbc:sqlite:papermacros.db");

            TableUtils.createTableIfNotExists(connectionSource, Macro.class);
            macrosDao = DaoManager.createDao(connectionSource, Macro.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<Macro, Long> getMacrosDao() {
        return macrosDao;
    }
}
