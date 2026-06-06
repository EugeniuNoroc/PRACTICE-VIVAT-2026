package com.university.library.repository;

import com.university.library.model.Loan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class LoanRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Loan> LOAN_ROW_MAPPER = (rs, rowNum) -> {
      return new Loan(rs.getObject("id", UUID.class), rs.getObject("book_id", UUID.class), rs.getObject("reader_id", UUID.class), rs.getDate("loan_date").toLocalDate(), rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null, rs.getBoolean("returned"));
    };

    public LoanRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Loan> findAll() {
        return jdbcTemplate.query("SELECT * FROM loans", LOAN_ROW_MAPPER);
    }

    public Optional<Loan> findById(UUID id) {
        return jdbcTemplate.query("SELECT * FROM loans WHERE id = ?", LOAN_ROW_MAPPER, id).stream().findFirst();
    }

    public void save(Loan loan) {
        jdbcTemplate.update(
                "INSERT INTO loans (book_id, reader_id, loan_date, return_date, returned) VALUES (?, ?, ?, ?, ?)",
                loan.getBookId(), loan.getReaderId(), loan.getLoanDate(), loan.getReturnDate(), loan.isReturned()
        );
    }

    public void update(Loan loan) {
        jdbcTemplate.update(
              "UPDATE loans SET book_id=?, reader_id=?, loan_date=?, return_date=?, returned=? WHERE id=?",
                loan.getBookId(), loan.getReaderId(), loan.getLoanDate(), loan.getReturnDate(), loan.isReturned(), loan.getId()
        );
    }

    public void delete(UUID id) {
        jdbcTemplate.update("DELETE FROM loans WHERE id = ?", id);
    }
}
