package com.proptech.backend.domain.exception;

import jakarta.persistence.EntityNotFoundException;

public class PropertyNotFoundException extends EntityNotFoundException {
    public PropertyNotFoundException(String id) {
        super("Inmueble no encontrado: " + id);
    }
}
