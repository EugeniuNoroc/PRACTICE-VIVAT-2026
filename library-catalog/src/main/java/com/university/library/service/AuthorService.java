package com.university.library.service;

import com.university.library.controller.dto.AuthorUpdateRequest;
import com.university.library.exception.AuthorNotFoundException;
import com.university.library.model.Author;
import com.university.library.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    public Optional<Author> findById(UUID id) {
        return authorRepository.findById(id);
    }

    public void save(Author author) {
        authorRepository.save(author);
    }

    public void update(UUID id, AuthorUpdateRequest request) {
        Author existingAuthor = authorRepository.findById(id).orElseThrow(() -> new AuthorNotFoundException(id));
        Author update = new Author(
                existingAuthor.getId(),
                request.fullName(),
                request.birthYear(),
                request.biography()
        );
        authorRepository.update(update);
    }

    public void delete(UUID id) {
        authorRepository.delete(id);
    }
}