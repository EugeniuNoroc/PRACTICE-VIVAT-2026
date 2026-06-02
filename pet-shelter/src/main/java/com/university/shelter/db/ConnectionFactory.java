package com.university.shelter.db;

import com.university.shelter.exception.DaoException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

    public static Connection getConnection() {
        Properties props = new Properties();

        try (InputStream inputStream =
                     ConnectionFactory.class.getClassLoader()
                             .getResourceAsStream("application.properties")) {

            if (inputStream == null) {
                throw new DaoException(
                        "Файл application.properties не найден. " +
                                "Скопируйте application.properties.example"
                );
            }

            props.load(inputStream);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.username");
            String password = props.getProperty("db.password");

            return DriverManager.getConnection(url, user, password);

        } catch (SQLException | IOException e) {
            throw new DaoException("Ошибка подключения к БД", e);
        }
    }
}