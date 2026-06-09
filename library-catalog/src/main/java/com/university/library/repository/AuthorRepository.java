package com.university.library.repository;

import com.university.library.model.Author;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AuthorRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Author> AUTHOR_ROW_MAPPER = (rs, rowNum) -> new Author(rs.getObject("id", UUID.class), rs.getString("full_name"), rs.getInt("birth_year"), rs.getString("biography"));

    public AuthorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Author> findAll() {
        return jdbcTemplate.query("SELECT * FROM authors", AUTHOR_ROW_MAPPER);
    }

    public Optional<Author> findById(UUID id) {
        return jdbcTemplate.query("SELECT * FROM authors WHERE id = ?", AUTHOR_ROW_MAPPER, id).stream().findFirst();
    }

    public void save(Author author) {
        jdbcTemplate.update(
                "INSERT INTO authors (full_name, birth_year, biography) VALUES (?, ?, ?)",
                author.getFullName(), author.getBirthYear(), author.getBiography()
        );
    }

    public void update(Author author) {
        jdbcTemplate.update(
                "UPDATE authors SET full_name=?, birth_year=?, biography=? WHERE id=?",
                author.getFullName(), author.getBirthYear(), author.getBiography(), author.getId()
        );
    }

    public void delete(UUID id) {
        jdbcTemplate.update("DELETE FROM authors WHERE id = ?", id);
    }
}
