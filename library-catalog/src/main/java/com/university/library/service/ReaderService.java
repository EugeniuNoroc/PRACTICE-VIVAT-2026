package com.university.library.service;

import com.university.library.controller.dto.ReaderUpdateRequest;
import com.university.library.exception.ReaderNotFoundException;
import com.university.library.model.Reader;
import com.university.library.repository.ReaderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReaderService {

    private final ReaderRepository readerRepository;

    public ReaderService(ReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
    }

    public List<Reader> findAll() {
        return readerRepository.findAll();
    }

    public Optional<Reader> findById(UUID id) {
        return readerRepository.findById(id);
    }

    public void save(Reader reader) {
        readerRepository.save(reader);
    }

    public void update(UUID id, ReaderUpdateRequest request) {
        Reader existing = readerRepository.findById(id)
                .orElseThrow(() -> new ReaderNotFoundException(id));
        Reader updated = new Reader(
                existing.getId(),
                request.fullName(),
                request.email(),
                existing.getRegistrationDate()
        );
        readerRepository.update(updated);
    }

    public void delete(UUID id) {
        readerRepository.delete(id);
    }
}