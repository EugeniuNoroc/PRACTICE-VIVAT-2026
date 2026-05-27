package com.university.shelter.db;

import com.university.shelter.exception.DaoException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

    public static Connection getConnection() {
        try {
            Properties props = new Properties();
            props.load(ConnectionFactory.class.getClassLoader().getResourceAsStream("application.properties"));
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.username");
            String password = props.getProperty("db.password");
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException | IOException e) {
            throw new DaoException("Ошибка подключения к БД", e);
        }
    }
}
