package com.proptech.backend.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@Slf4j
public class PdfExtractorService {

    public record ExtractionResult(String text, boolean readable) {}

    /**
     * Extrae texto plano de un PDF en memoria.
     * Ningún byte del PDF se escribe a disco ni se persiste.
     * Si el PDF está escaneado (sin texto) o es inválido, retorna readable=false sin propagar excepción.
     */
    public ExtractionResult extract(InputStream in) {
        try (PDDocument doc = PDDocument.load(in)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);
            if (text == null || text.isBlank()) {
                return new ExtractionResult("", false);
            }
            return new ExtractionResult(text, true);
        } catch (Exception e) {
            log.warn("PdfExtractorService: no se pudo extraer texto del PDF — {}", e.getMessage());
            return new ExtractionResult("", false);
        }
    }
}
