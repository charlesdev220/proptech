package com.proptech.backend.domain.exception;

import jakarta.persistence.EntityNotFoundException;

public class MediaNotFoundException extends EntityNotFoundException {
    public MediaNotFoundException(String id) {
        super("Media no encontrado con id: " + id);
    }
}
