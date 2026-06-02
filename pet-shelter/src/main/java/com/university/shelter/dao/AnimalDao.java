package com.university.shelter.dao;

import com.university.shelter.model.Animal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * REVIEW[THINK] (W1, день 3): интерфейс смешивает ДВЕ роли:
 *   1) CRUD-доступ к данным: save / findById / findAll / findByType / update / delete — это и есть DAO.
 *   2) Аналитику: findHeaviest / findOlderThan / averageAge / countByType — это уже бизнес-логика.
 *
 * Из-за этого JDBC-реализация (AnimalDaoJdbc) вынуждена "реализовывать" аналитику,
 * которую не умеет, и бросает UnsupportedOperationException. Это нарушение
 * принципа подстановки Лисков (LSP): клиент не может одинаково полагаться на любую
 * реализацию AnimalDao — InMemory работает, JDBC падает.
 *
 * Варианты:
 *   - Оставить в DAO только CRUD, а аналитику перенести в ShelterService
 *     (он возьмёт findAll() и посчитает через Stream — как ты уже умеешь).
 *   - Либо честно реализовать аналитику в SQL во ВСЕХ реализациях (SELECT ... ORDER BY,
 *     AVG(...), GROUP BY ...).
 * REVIEW[THINK]: какой вариант лучше для производительности на 1 000 000 животных и почему?
 */
public interface AnimalDao {
    void save(Animal animal);
    Optional<Animal> findById(UUID id);
    List<Animal> findAll();
    List<Animal> findByType(Class<? extends Animal> type);
    void update(Animal animal);
    void delete(UUID id);
    Optional<Animal> findHeaviest();
    List<Animal> findOlderThan(int years);
    double averageAge();
    Map<Class<? extends Animal>, Long> countByType();
}
