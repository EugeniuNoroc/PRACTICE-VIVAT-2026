# Spring JDBC vs голый JDBC

## Что исчезло в W2

В W1 каждый метод в `AnimalDaoJdbc` требовал:
- открытие `Connection` через `ConnectionFactory`
- создание `PreparedStatement` внутри `try-with-resources`
- ручное закрытие `ResultSet`, `PreparedStatement`, `Connection`
- перехват `SQLException` и оборачивание в `DaoException`
- создание и управление `ConnectionFactory` вручную

В W2 `JdbcTemplate` берёт всё это на себя. Метод `findAll` занимает одну строку вместо 15.

## Что осталось

- SQL запросы пишутся вручную
- маппинг колонок в поля объекта через `RowMapper`

## Главный вывод

`JdbcTemplate` не скрывает SQL — он скрывает boilerplate вокруг SQL.