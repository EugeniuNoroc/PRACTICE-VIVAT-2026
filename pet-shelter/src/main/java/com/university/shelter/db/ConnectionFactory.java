
package com.university.shelter.db;

import com.university.shelter.exception.DaoException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * REVIEW[BUG] (W1, день 3): getResourceAsStream вернёт null, если
 * application.properties нет в classpath, и тогда Properties.load(null) кидает
 * NPE с невнятным текстом "inStream parameter is null". Именно поэтому у нового
 * разработчика (или в CI без файла) падает запуск.
 *
 * Что улучшить:
 *  1) Проверить поток на null и бросить ПОНЯТНЫЙ DaoException
 *     ("application.properties не найден; скопируй application.properties.example").
 *  2) Обернуть InputStream в try-with-resources (сейчас поток не закрывается — утечка ресурса).
 *
 * REVIEW[THINK]: каждый вызов getConnection() открывает НОВОЕ физическое соединение
 * и заново читает файл с диска. Прикинь стоимость на 1000 операций. Чем это лечат?
 * (ответ — connection pool, познакомишься с HikariCP в W2).
 */

public class ConnectionFactory {

    public static Connection getConnection() {
        Properties props = new Properties();

        try (InputStream inputStream =
                     ConnectionFactory.class.getClassLoader()
                             .getResourceAsStream("application.properties")) {

            if (inputStream == null) {
                throw new DaoException(
                        "application.properties не найден. Скопируйте application.properties.example",
                        null
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