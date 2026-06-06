package com.university.library.repository;

import com.university.library.model.Reader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ReaderRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Reader> READER_ROW_MAPPER = (rs, rowNum) -> {
        return new Reader(rs.getObject("id", UUID.class), rs.getString("full_name"), rs.getString("email"), rs.getDate("registration_date").toLocalDate());
    };

    public ReaderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Reader> findAll() {
        return jdbcTemplate.query("SELECT * FROM readers", READER_ROW_MAPPER);
    }

    public Optional<Reader> findById(UUID id) {
        return jdbcTemplate.query("SELECT * FROM readers WHERE id = ?", READER_ROW_MAPPER, id).stream().findFirst();
    }

    public void save(Reader reader) {
        jdbcTemplate.update(
                "INSERT INTO readers (full_name, email, registration_date) VALUES (?, ?, ?)",
                reader.getFullName(), reader.getEmail(), reader.getRegistrationDate()
        );
    }

    public void update(Reader reader) {
        jdbcTemplate.update(
                "UPDATE readers SET full_name=?, email=?, registration_date=? WHERE id=?",
                reader.getFullName(), reader.getEmail(), reader.getRegistrationDate(),  reader.getId()
        );
    }

    public void delete(UUID id) {
        jdbcTemplate.update("DELETE FROM readers WHERE id = ?", id);
    }
}