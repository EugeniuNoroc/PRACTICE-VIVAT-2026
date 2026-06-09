package com.university.library.repository;

import com.university.library.model.Book;
import com.university.library.model.BookWithAuthor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class BookRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final RowMapper<Book> BOOK_ROW_MAPPER = (rs, rowNum) -> new Book(rs.getObject("id", UUID.class), rs.getString("title"), rs.getString("isbn"), rs.getInt("year"), rs.getObject("author_id", UUID.class), rs.getInt("copies_total"), rs.getInt("copies_available"));

    public BookRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public List<Book> findAll() {
        return jdbcTemplate.query("SELECT * FROM books", BOOK_ROW_MAPPER);
    }

    public List<Book> findAll(int offset, int limit) {
        return jdbcTemplate.query(
                "SELECT * FROM books LIMIT ? OFFSET ?",
                BOOK_ROW_MAPPER, limit, offset
        );
    }

    public long count() {
        Long result = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM books", Long.class);
        return result != null ? result : 0L;
    }

    public Optional<Book> findById(UUID id) {
        // return jdbcTemplate.query("SELECT * FROM books WHERE id = ?", BOOK_ROW_MAPPER, id).stream().findFirst();
        return namedParameterJdbcTemplate.query(
                "SELECT * FROM books WHERE id = :id",
                Map.of("id", id), BOOK_ROW_MAPPER).stream().findFirst();
    }

    public void save(Book book) {
        /*
        jdbcTemplate.update(
                "INSERT INTO books (title, isbn, year, author_id, copies_total, copies_available) VALUES (?,?,?,?,?,?)",
                book.getTitle(), book.getIsbn(), book.getYear(), book.getAuthorId(), book.getCopiesTotal(), book.getCopiesAvailable()
        );
        */
        namedParameterJdbcTemplate.update("INSERT INTO books (title, isbn, year, author_id, copies_total, copies_available) VALUES (:title, :isbn, :year, :authorId, :copiesTotal, :copiesAvailable)",
                Map.of("title", book.getTitle(), "isbn", book.getIsbn(), "year", book.getYear(), "authorId", book.getAuthorId(), "copiesTotal", book.getCopiesTotal(), "copiesAvailable", book.getCopiesAvailable())
        );
    }

    public void update(Book book) {
        jdbcTemplate.update(
                "UPDATE books SET title=?, isbn=?, year=?, author_id=?, copies_total=?, copies_available=? WHERE id=?",
                book.getTitle(), book.getIsbn(), book.getYear(), book.getAuthorId(),
                book.getCopiesTotal(), book.getCopiesAvailable(), book.getId()
        );
    }

    public void delete(UUID id) {
        jdbcTemplate.update("DELETE FROM books WHERE id = ?", id);
    }

    public List<BookWithAuthor> findAllBooksWithAuthor() {
        return jdbcTemplate.query(
                "SELECT b.id, b.title, a.full_name, as author_name " + "FROM books b JOIN authors a ON b.author_id = a.id",
                (rs, rowNum) -> new BookWithAuthor(
                        rs.getObject("id", UUID.class),
                        rs.getString("title"),
                        rs.getString("isbn"),
                        rs.getString("author_name")
                )
        );
    }

    public Map<UUID, Integer> countBooksByAuthor() {
        return jdbcTemplate.query(
                // SQL: считаем количество книг для каждого автора и группируем по author_id
                "SELECT author_id, COUNT(*) as book_count FROM books GROUP BY author_id",
                rs -> {
                    // ResultSetExtractor - получаем весь ResultSet сразу, сами обходим строки
                    // В отличие от RowMapper который вызывается на каждую строку автоматически
                    Map<UUID, Integer> result = new HashMap<>();
                    while (rs.next()) {
                        // для каждой строки кладём в словарь: id автора - количество его книг
                        result.put(
                                rs.getObject("author_id", UUID.class),
                                rs.getInt("book_count")
                        );
                    }
                    return result;
                }
        );
    }
}
