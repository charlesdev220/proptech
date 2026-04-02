package com.proptech.backend.api.controller;

import com.proptech.backend.domain.service.MediaService;
import com.proptech.backend.domain.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProfileControllerDocumentTest {

    private final ProfileService profileService = Mockito.mock(ProfileService.class);
    private final MediaService mediaService = Mockito.mock(MediaService.class);
    private final ProfileController controller = new ProfileController(profileService, mediaService);

    @Test
    void validJpegFile_returns201WithMediaDTO() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
            "file", "dni.jpg", "image/jpeg", new byte[1024]
        );
        when(mediaService.saveMedia(any())).thenReturn("uuid-1234");

        var response = controller.uploadDocument(file);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("dni.jpg", response.getBody().getFileName());
        assertEquals(1024L, response.getBody().getSize());
    }

    @Test
    void validPdfFile_returns201() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
            "file", "nomina.pdf", "application/pdf", new byte[2048]
        );
        when(mediaService.saveMedia(any())).thenReturn("uuid-5678");

        var response = controller.uploadDocument(file);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void fileTooLarge_throwsIllegalArgumentException() {
        byte[] bigData = new byte[6 * 1024 * 1024]; // 6MB
        MockMultipartFile file = new MockMultipartFile(
            "file", "big.jpg", "image/jpeg", bigData
        );

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> controller.uploadDocument(file)
        );
        assertEquals("El archivo supera el límite de 5MB", ex.getMessage());
    }

    @Test
    void invalidContentType_throwsIllegalArgumentException() {
        MockMultipartFile file = new MockMultipartFile(
            "file", "video.mp4", "video/mp4", new byte[1024]
        );

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> controller.uploadDocument(file)
        );
        assertTrue(ex.getMessage().contains("Tipo de archivo no permitido"));
    }
}
