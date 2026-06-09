package com.university.library.controller;

import com.university.library.controller.dto.ReaderCreateRequest;
import com.university.library.controller.dto.ReaderResponse;
import com.university.library.controller.dto.ReaderUpdateRequest;
import com.university.library.model.Reader;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ReaderMapper {

    public ReaderResponse toResponse(Reader reader) {
        return new ReaderResponse(
                reader.getId(),
                reader.getFullName(),
                reader.getEmail(),
                reader.getRegistrationDate()
        );
    }

    public Reader toEntity(ReaderCreateRequest request) {
        return new Reader(
                UUID.randomUUID(),
                request.fullName(),
                request.email(),
                request.registrationDate()
        );
    }
}