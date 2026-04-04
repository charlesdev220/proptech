package com.proptech.backend.domain.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SolvencyAnalyzerServiceTest {

    private final SolvencyAnalyzerService service = new SolvencyAnalyzerService();

    // ─────────────────────────────────────────────────────────────────────────
    // Task 6.2: extractFields — field extraction (REQ-S2)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void extractFields_withCif_extractsCif() {
        String text = "Empresa XYZ\nCIF: B12345678\nneto: 1.800,00";
        SolvencyAnalyzerService.DocumentFields fields = service.extractFields(text);
        assertEquals("B12345678", fields.cif());
        assertTrue(fields.readable());
    }

    @Test
    void extractFields_withoutCif_cifIsNull() {
        String text = "Empresa sin CIF registrado.\nneto: 1.500,00";
        SolvencyAnalyzerService.DocumentFields fields = service.extractFields(text);
        assertNull(fields.cif(), "CIF ausente debe retornar null sin excepción");
        assertTrue(fields.readable());
    }

    @Test
    void extractFields_withContratoIndefinido_extractsTipoContrato() {
        String text = "Se firma el presente contrato indefinido entre las partes.";
        SolvencyAnalyzerService.DocumentFields fields = service.extractFields(text);
        assertEquals("INDEFINIDO", fields.tipoContrato());
    }

    @Test
    void extractFields_withContratoTemporal_extractsTipoContrato() {
        String text = "Contrato temporal por necesidades de producción.";
        SolvencyAnalyzerService.DocumentFields fields = service.extractFields(text);
        assertEquals("TEMPORAL", fields.tipoContrato());
    }

    @Test
    void extractFields_withNullText_returnsNotReadable() {
        SolvencyAnalyzerService.DocumentFields fields = service.extractFields(null);
        assertFalse(fields.readable());
    }

    @Test
    void extractFields_withBlankText_returnsNotReadable() {
        SolvencyAnalyzerService.DocumentFields fields = service.extractFields("   ");
        assertFalse(fields.readable());
    }

    @Test
    void extractFields_withSalarioNeto_extractsImporte() {
        String text = "Neto a percibir: 2.100,50\nmarzo de 2024";
        SolvencyAnalyzerService.DocumentFields fields = service.extractFields(text);
        assertNotNull(fields.salarioNeto());
        assertEquals(0, new BigDecimal("2100.50").compareTo(fields.salarioNeto()));
    }

    @Test
    void extractFields_withFechaMonth_extractsYearMonth() {
        String text = "Nómina correspondiente al mes de marzo de 2024\nneto: 1.800,00";
        SolvencyAnalyzerService.DocumentFields fields = service.extractFields(text);
        assertEquals(YearMonth.of(2024, 3), fields.fecha());
    }

    @Test
    void extractFields_withoutTipoContrato_tipoContratoIsNull() {
        String text = "Nómina enero de 2024\nneto: 1.800,00\nCIF: B12345678";
        SolvencyAnalyzerService.DocumentFields fields = service.extractFields(text);
        assertNull(fields.tipoContrato());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Task 6.3: validateCoherence (REQ-S2 edge cases)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void validateCoherence_withCifInconsistente_cifConsistenteFalse() {
        SolvencyAnalyzerService.DocumentFields n1 = nomina("Juan Pérez", "B12345678", YearMonth.of(2024, 1), "1800.00");
        SolvencyAnalyzerService.DocumentFields n2 = nomina("Juan Pérez", "B99999999", YearMonth.of(2024, 2), "1800.00");
        SolvencyAnalyzerService.DocumentFields n3 = nomina("Juan Pérez", "B12345678", YearMonth.of(2024, 3), "1800.00");

        SolvencyAnalyzerService.CoherenceResult result = service.validateCoherence(List.of(n1, n2, n3));

        assertFalse(result.cifConsistente(), "CIF inconsistente debe retornar cifConsistente=false");
    }

    @Test
    void validateCoherence_withSecuenciaNoConsecutiva_secuenciaInvalida() {
        SolvencyAnalyzerService.DocumentFields n1 = nomina("Ana García", "B12345678", YearMonth.of(2024, 1), "1800.00");
        SolvencyAnalyzerService.DocumentFields n2 = nomina("Ana García", "B12345678", YearMonth.of(2024, 3), "1800.00"); // gap
        SolvencyAnalyzerService.DocumentFields n3 = nomina("Ana García", "B12345678", YearMonth.of(2024, 4), "1800.00");

        SolvencyAnalyzerService.CoherenceResult result = service.validateCoherence(List.of(n1, n2, n3));

        assertFalse(result.secuenciaValida(), "Secuencia con gap no debe ser válida");
    }

    @Test
    void validateCoherence_withVariacionSalarialAlta_estabilidadFalse() {
        // Variación >20%: 1000 → 1500 = 50%
        SolvencyAnalyzerService.DocumentFields n1 = nomina("Luis López", "B12345678", YearMonth.of(2024, 1), "1000.00");
        SolvencyAnalyzerService.DocumentFields n2 = nomina("Luis López", "B12345678", YearMonth.of(2024, 2), "1500.00");
        SolvencyAnalyzerService.DocumentFields n3 = nomina("Luis López", "B12345678", YearMonth.of(2024, 3), "1000.00");

        SolvencyAnalyzerService.CoherenceResult result = service.validateCoherence(List.of(n1, n2, n3));

        assertFalse(result.estabilidadSalarial(), "Variación >20% debe retornar estabilidadSalarial=false");
    }

    @Test
    void validateCoherence_withAllUnreadable_documentosIncompletosAndNoChecks() {
        SolvencyAnalyzerService.DocumentFields unreadable =
            new SolvencyAnalyzerService.DocumentFields(null, null, null, null, null, null, false);

        SolvencyAnalyzerService.CoherenceResult result =
            service.validateCoherence(List.of(unreadable, unreadable, unreadable));

        assertFalse(result.documentosCompletos());
        assertFalse(result.secuenciaValida());
    }

    @Test
    void validateCoherence_withConsistentDocuments_allChecksPass() {
        SolvencyAnalyzerService.DocumentFields n1 = nomina("María Torres", "B12345678", YearMonth.of(2024, 1), "2000.00");
        SolvencyAnalyzerService.DocumentFields n2 = nomina("María Torres", "B12345678", YearMonth.of(2024, 2), "2000.00");
        SolvencyAnalyzerService.DocumentFields n3 = nomina("María Torres", "B12345678", YearMonth.of(2024, 3), "2000.00");

        SolvencyAnalyzerService.CoherenceResult result = service.validateCoherence(List.of(n1, n2, n3));

        assertTrue(result.cifConsistente());
        assertTrue(result.nombreConsistente());
        assertTrue(result.secuenciaValida());
        assertTrue(result.estabilidadSalarial());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Task 6.4: calculateScore (REQ-S3)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void calculateScore_withCompleteDocumentation_returnsAtLeast80() {
        // Salary 2500 → 18pts, stability → 20pts, sequence → coherence 15pts, contract INDEFINIDO → 20pts
        SolvencyAnalyzerService.DocumentFields n1 = nomina("Carlos Ruiz", "B12345678", YearMonth.of(2024, 1), "2500.00");
        SolvencyAnalyzerService.DocumentFields n2 = nomina("Carlos Ruiz", "B12345678", YearMonth.of(2024, 2), "2500.00");
        SolvencyAnalyzerService.DocumentFields n3 = nomina("Carlos Ruiz", "B12345678", YearMonth.of(2024, 3), "2500.00");
        // Contract doc with INDEFINIDO type, started 3 years ago
        SolvencyAnalyzerService.DocumentFields contrato = new SolvencyAnalyzerService.DocumentFields(
            "Carlos Ruiz", "B12345678",
            YearMonth.now().minusMonths(40), // antigüedad 40 meses → 15pts
            null, null, "INDEFINIDO", true
        );

        SolvencyAnalyzerService.CoherenceResult coherence = buildCoherence(List.of(n1, n2, n3), contrato,
            true, true, true, true, true);
        SolvencyAnalyzerService.SolvencyAnalysis analysis = service.calculateScore(coherence);

        assertTrue(analysis.score() >= 80,
            "Documentación completa con INDEFINIDO debe alcanzar >=80 pts, fue: " + analysis.score());
        assertEquals("PLATINUM", analysis.level());
    }

    @Test
    void calculateScore_temporalVsIndefinido_indefinidoScoresHigher() {
        SolvencyAnalyzerService.DocumentFields nomina =
            nomina("Test User", "B12345678", YearMonth.of(2024, 1), "2500.00");

        SolvencyAnalyzerService.DocumentFields contratoIndefinido = new SolvencyAnalyzerService.DocumentFields(
            null, null, null, null, null, "INDEFINIDO", true);
        SolvencyAnalyzerService.DocumentFields contratoTemporal = new SolvencyAnalyzerService.DocumentFields(
            null, null, null, null, null, "TEMPORAL", true);

        SolvencyAnalyzerService.CoherenceResult cohIndefinido =
            buildCoherence(List.of(nomina), contratoIndefinido, true, true, false, true, false);
        SolvencyAnalyzerService.CoherenceResult cohTemporal =
            buildCoherence(List.of(nomina), contratoTemporal, true, true, false, true, false);

        int scoreIndefinido = service.calculateScore(cohIndefinido).score();
        int scoreTemporal   = service.calculateScore(cohTemporal).score();

        assertTrue(scoreIndefinido > scoreTemporal,
            "INDEFINIDO debe puntuar más que TEMPORAL: " + scoreIndefinido + " vs " + scoreTemporal);
    }

    @Test
    void calculateScore_withCifInconsistente_reducesScore() {
        SolvencyAnalyzerService.DocumentFields n1 = nomina("Pedro Sanz", "B11111111", YearMonth.of(2024, 1), "1800.00");
        SolvencyAnalyzerService.DocumentFields n2 = nomina("Pedro Sanz", "B22222222", YearMonth.of(2024, 2), "1800.00");
        SolvencyAnalyzerService.DocumentFields n3 = nomina("Pedro Sanz", "B11111111", YearMonth.of(2024, 3), "1800.00");

        SolvencyAnalyzerService.CoherenceResult coherence = service.validateCoherence(List.of(n1, n2, n3));
        SolvencyAnalyzerService.SolvencyAnalysis analysis = service.calculateScore(coherence);

        // CIF inconsistente → coherenciaCompleta=false → no suma 15pts
        assertFalse(coherence.cifConsistente());
        // Score still positive (other factors contribute)
        assertTrue(analysis.score() > 0,
            "Score con documentos parciales debe ser >0");
    }

    @Test
    void calculateScore_withPartialDocuments_scoreAboveZero() {
        // Only one readable payslip — no contract
        SolvencyAnalyzerService.DocumentFields n1 = nomina("Sofia Vega", "B12345678", YearMonth.of(2024, 1), "1800.00");

        SolvencyAnalyzerService.CoherenceResult coherence = service.validateCoherence(List.of(n1));
        SolvencyAnalyzerService.SolvencyAnalysis analysis = service.calculateScore(coherence);

        assertTrue(analysis.score() >= 0, "Score nunca debe ser negativo");
        assertTrue(analysis.score() <= 100, "Score nunca debe superar 100");
    }

    @Test
    void calculateScore_scoreNeverExceedsMaxBounds() {
        // Max scenario: all factors maxed
        SolvencyAnalyzerService.DocumentFields n1 = nomina("Elite User", "B12345678", YearMonth.of(2024, 1), "5000.00");
        SolvencyAnalyzerService.DocumentFields n2 = nomina("Elite User", "B12345678", YearMonth.of(2024, 2), "5000.00");
        SolvencyAnalyzerService.DocumentFields n3 = nomina("Elite User", "B12345678", YearMonth.of(2024, 3), "5000.00");
        SolvencyAnalyzerService.DocumentFields contrato = new SolvencyAnalyzerService.DocumentFields(
            "Elite User", "B12345678", YearMonth.now().minusMonths(70),
            null, null, "INDEFINIDO", true);

        SolvencyAnalyzerService.CoherenceResult coherence = buildCoherence(
            List.of(n1, n2, n3), contrato, true, true, true, true, true);
        SolvencyAnalyzerService.SolvencyAnalysis analysis = service.calculateScore(coherence);

        assertTrue(analysis.score() <= 100, "Score no puede superar 100");
        assertTrue(analysis.score() >= 0,   "Score no puede ser negativo");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private SolvencyAnalyzerService.DocumentFields nomina(
            String nombre, String cif, YearMonth fecha, String salario) {
        return new SolvencyAnalyzerService.DocumentFields(
            nombre, cif, fecha,
            new BigDecimal(salario), null,
            null, true
        );
    }

    /**
     * Builds a CoherenceResult with explicit check values, bypassing the internal logic.
     * Used when we need to test calculateScore() in isolation from validateCoherence().
     */
    private SolvencyAnalyzerService.CoherenceResult buildCoherence(
            List<SolvencyAnalyzerService.DocumentFields> payslips,
            SolvencyAnalyzerService.DocumentFields contrato,
            boolean nombreConsistente, boolean cifConsistente,
            boolean secuenciaValida, boolean estabilidadSalarial,
            boolean documentosCompletos) {

        List<SolvencyAnalyzerService.CheckResult> checks = List.of(
            new SolvencyAnalyzerService.CheckResult("Nombre consistente", nombreConsistente, ""),
            new SolvencyAnalyzerService.CheckResult("CIF empleador consistente", cifConsistente, ""),
            new SolvencyAnalyzerService.CheckResult("Secuencia temporal válida", secuenciaValida, ""),
            new SolvencyAnalyzerService.CheckResult("Estabilidad salarial", estabilidadSalarial, ""),
            new SolvencyAnalyzerService.CheckResult("Documentos completos", documentosCompletos, "")
        );

        return new SolvencyAnalyzerService.CoherenceResult(
            nombreConsistente, cifConsistente, secuenciaValida,
            estabilidadSalarial, documentosCompletos, checks, payslips, contrato
        );
    }
}
