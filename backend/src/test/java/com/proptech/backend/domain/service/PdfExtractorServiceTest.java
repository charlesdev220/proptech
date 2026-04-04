package com.proptech.backend.domain.service;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class PdfExtractorServiceTest {

    private final PdfExtractorService service = new PdfExtractorService();

    @Test
    void extract_withEmptyStream_returnsUnreadable() {
        // Un stream vacío no es un PDF válido → debe retornar readable=false sin excepción
        InputStream empty = new ByteArrayInputStream(new byte[0]);
        PdfExtractorService.ExtractionResult result = service.extract(empty);
        assertFalse(result.readable(), "Stream vacío debe retornar readable=false");
        assertNotNull(result.text(), "text no debe ser null");
    }

    @Test
    void extract_withInvalidBytes_returnsUnreadable() {
        // Bytes arbitrarios que no son un PDF válido
        byte[] garbage = "not a pdf at all %%BOGUS".getBytes();
        PdfExtractorService.ExtractionResult result = service.extract(new ByteArrayInputStream(garbage));
        assertFalse(result.readable(), "Contenido no-PDF debe retornar readable=false");
    }

    @Test
    void extract_withInvalidStream_doesNotThrow() {
        // InputStream que lanza excepción en read → no debe propagar
        InputStream broken = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Simulated IO failure");
            }
        };
        assertDoesNotThrow(() -> service.extract(broken),
            "Un InputStream roto no debe propagar la excepción");
        PdfExtractorService.ExtractionResult result = service.extract(new ByteArrayInputStream(new byte[0]));
        assertFalse(result.readable());
    }
}
