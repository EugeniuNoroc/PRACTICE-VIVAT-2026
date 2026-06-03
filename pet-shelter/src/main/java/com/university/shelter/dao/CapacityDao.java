package com.university.shelter.dao;

import com.university.shelter.db.ConnectionFactory;
import com.university.shelter.exception.DaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CapacityDao {

    private static final Logger logger = LoggerFactory.getLogger(CapacityDao.class);

    // Версия 1: наивная - без транзакции, autocommit=true по умолчанию
    public boolean tryReserveNaive() {
        try (Connection conn = ConnectionFactory.getConnection()) {

            int spots;
            try (PreparedStatement select = conn.prepareStatement(
                    "SELECT available_spots FROM shelter_capacity WHERE id = 1")) {
                ResultSet rs = select.executeQuery();
                rs.next();
                spots = rs.getInt("available_spots");
            }

            if (spots > 0) {
                try (PreparedStatement update = conn.prepareStatement(
                        "UPDATE shelter_capacity SET available_spots = ? WHERE id = 1")) {
                    update.setInt(1, spots - 1);
                    update.executeUpdate();
                }
                logger.debug("Naive: забронировано, осталось мест примерно {}", spots - 1);
                return true;
            }
            return false;

        } catch (SQLException e) {
            throw new DaoException("Ошибка при наивном бронировании", e);
        }
    }
    /*
    *      === NAIVE ===
    * Успешных бронирований: 14/12/15
    * Осталось мест в БД: 0
    * Потеряно обновлений: 4/2/5
    * */

    // Версия 2: с SELECT ... FOR UPDATE - пессимистичная блокировка строки
    public boolean tryReserveForUpdate() {
        Connection conn = ConnectionFactory.getConnection();
        try {
            conn.setAutoCommit(false);

            int spots;
            try (PreparedStatement select = conn.prepareStatement(
                    "SELECT available_spots FROM shelter_capacity WHERE id = 1 FOR UPDATE")) {
                ResultSet rs = select.executeQuery();
                rs.next();
                spots = rs.getInt("available_spots");
            }

            if (spots > 0) {
                try (PreparedStatement update = conn.prepareStatement(
                        "UPDATE shelter_capacity SET available_spots = ? WHERE id = 1")) {
                    update.setInt(1, spots - 1);
                    update.executeUpdate();
                }
                conn.commit();
                logger.debug("ForUpdate: забронировано, осталось {}", spots - 1);
                return true;
            }

            conn.commit();
            return false;

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                throw new DaoException("Ошибка при rollback", rollbackEx);
            }
            throw new DaoException("Ошибка при бронировании с FOR UPDATE", e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.warn("Не удалось закрыть соединение", e);
            }
        }
    }
    /*
    *    === FOR UPDATE ===
    * Успешных бронирований: 10/10/10
    * Осталось мест в БД: 0
    * */

    // Вспомогательный метод для тестов - сбросить счётчик обратно к заданному значению
    public void resetSpots(int value) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE shelter_capacity SET available_spots = ? WHERE id = 1")) {
            stmt.setInt(1, value);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Ошибка при сбросе счётчика", e);
        }
    }

    // Вспомогательный метод - прочитать текущее значение
    public int getCurrentSpots() {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT available_spots FROM shelter_capacity WHERE id = 1")) {
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt("available_spots");
        } catch (SQLException e) {
            throw new DaoException("Ошибка при чтении счётчика", e);
        }
    }
}