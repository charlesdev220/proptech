package com.proptech.backend.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SolvencyAnalyzerService {

    // --- Records públicos ---

    public record DocumentFields(
        String nombre,
        String cif,
        YearMonth fecha,
        BigDecimal salarioNeto,
        BigDecimal salarioBruto,
        String tipoContrato,
        boolean readable
    ) {}

    public record CheckResult(String name, boolean passed, String description) {}

    public record CoherenceResult(
        boolean nombreConsistente,
        boolean cifConsistente,
        boolean secuenciaValida,
        boolean estabilidadSalarial,
        boolean documentosCompletos,
        List<CheckResult> checks,
        List<DocumentFields> payslips,
        DocumentFields contract
    ) {}

    public record SolvencyAnalysis(
        int score,
        String level,
        List<CheckResult> checks,
        boolean documentosCompletos,
        String contractType
    ) {}

    // --- Regex patterns ---
    private static final Pattern CIF_PATTERN =
        Pattern.compile("\\b([A-Z]\\d{8})\\b");

    private static final Pattern NOMBRE_PATTERN =
        Pattern.compile("(?i)(?:trabajador|empleado|nombre)[:\\s]+([A-ZÁÉÍÓÚÑ][a-záéíóúñA-ZÁÉÍÓÚÑ\\s]{5,50})");

    private static final Pattern SALARIO_NETO_PATTERN =
        Pattern.compile("(?i)(?:neto|líquido|a percibir)[:\\s]*([0-9]{1,4}[.,][0-9]{2})");

    private static final Pattern SALARIO_BRUTO_PATTERN =
        Pattern.compile("(?i)(?:bruto|total devengado)[:\\s]*([0-9]{1,4}[.,][0-9]{2})");

    private static final Pattern FECHA_NOMINA_PATTERN =
        Pattern.compile("(?i)(?:enero|febrero|marzo|abril|mayo|junio|julio|agosto|septiembre|octubre|noviembre|diciembre)\\s+(?:de\\s+)?(20\\d{2})");

    private static final Map<String, Integer> MONTH_MAP = Map.ofEntries(
        Map.entry("enero", 1), Map.entry("febrero", 2), Map.entry("marzo", 3),
        Map.entry("abril", 4), Map.entry("mayo", 5), Map.entry("junio", 6),
        Map.entry("julio", 7), Map.entry("agosto", 8), Map.entry("septiembre", 9),
        Map.entry("octubre", 10), Map.entry("noviembre", 11), Map.entry("diciembre", 12)
    );

    private static final Pattern CONTRATO_INDEFINIDO_PATTERN =
        Pattern.compile("(?i)contrato\\s+(?:de\\s+trabajo\\s+)?(?:por\\s+tiempo\\s+)?indefinido");

    private static final Pattern CONTRATO_TEMPORAL_PATTERN =
        Pattern.compile("(?i)(?:contrato\\s+temporal|obra\\s+y\\s+servicio|por\\s+obra|eventual)");

    // --- Métodos públicos ---

    /**
     * Extrae campos semánticos del texto plano de un documento.
     * Campos no encontrados quedan como null sin lanzar excepción.
     */
    public DocumentFields extractFields(String text) {
        if (text == null || text.isBlank()) {
            return new DocumentFields(null, null, null, null, null, null, false);
        }

        String nombre = extractNombre(text);
        String cif = extractCif(text);
        YearMonth fecha = extractFecha(text);
        BigDecimal salarioNeto = extractImporte(text, SALARIO_NETO_PATTERN);
        BigDecimal salarioBruto = extractImporte(text, SALARIO_BRUTO_PATTERN);
        String tipoContrato = extractTipoContrato(text);

        return new DocumentFields(nombre, cif, fecha, salarioNeto, salarioBruto, tipoContrato, true);
    }

    /**
     * Valida coherencia cruzada entre los documentos extraídos.
     * No falla rápido — evalúa todos los checks aunque alguno falle.
     */
    public CoherenceResult validateCoherence(List<DocumentFields> docs) {
        List<DocumentFields> legibles = docs.stream().filter(d -> d.readable()).toList();
        List<DocumentFields> nominas = legibles.stream()
            .filter(d -> d.fecha() != null && d.salarioNeto() != null)
            .toList();
        List<DocumentFields> contratos = legibles.stream()
            .filter(d -> d.tipoContrato() != null && d.fecha() == null)
            .toList();

        DocumentFields contrato = contratos.isEmpty() ? null : contratos.get(0);
        boolean documentosCompletos = docs.stream().allMatch(d -> d.readable()) && nominas.size() >= 3;

        boolean nombreConsistente = checkNombreConsistente(legibles);
        boolean cifConsistente = checkCifConsistente(legibles);
        boolean secuenciaValida = checkSecuenciaValida(nominas);
        boolean estabilidadSalarial = checkEstabilidadSalarial(nominas);

        List<CheckResult> checks = List.of(
            new CheckResult("Nombre consistente", nombreConsistente,
                nombreConsistente ? "El nombre del trabajador coincide en todos los documentos."
                    : "El nombre no coincide entre todos los documentos."),
            new CheckResult("CIF empleador consistente", cifConsistente,
                cifConsistente ? "El CIF del empleador coincide en todos los documentos."
                    : "El CIF del empleador varía entre documentos."),
            new CheckResult("Secuencia temporal válida", secuenciaValida,
                secuenciaValida ? "Las 3 nóminas son de meses consecutivos y recientes."
                    : "Las nóminas no cubren 3 meses consecutivos."),
            new CheckResult("Estabilidad salarial", estabilidadSalarial,
                estabilidadSalarial ? "La variación salarial entre nóminas es inferior al 20%."
                    : "La variación salarial supera el umbral del 20%."),
            new CheckResult("Documentos completos", documentosCompletos,
                documentosCompletos ? "Se han procesado correctamente todos los documentos."
                    : "Algún documento no pudo ser leído o faltan nóminas.")
        );

        return new CoherenceResult(
            nombreConsistente, cifConsistente, secuenciaValida,
            estabilidadSalarial, documentosCompletos, checks, nominas, contrato
        );
    }

    /**
     * Calcula el SolvencyScore ponderado (0-100) a partir del resultado de coherencia.
     */
    public SolvencyAnalysis calculateScore(CoherenceResult coherence) {
        int score = 0;

        // Factor 1: Estabilidad salarial (20 pts)
        if (coherence.estabilidadSalarial()) score += 20;

        // Factor 2: Nivel salarial — ratio salario medio / umbral mínimo viable 1500€ (25 pts)
        score += calcularPuntosNivelSalarial(coherence.payslips());

        // Factor 3: Antigüedad laboral (20 pts)
        score += calcularPuntosAntiguedad(coherence.contract());

        // Factor 4: Tipo de contrato (20 pts)
        int puntosContrato = 0;
        String contractType = null;
        if (coherence.contract() != null && coherence.contract().tipoContrato() != null) {
            contractType = coherence.contract().tipoContrato();
            puntosContrato = switch (contractType) {
                case "INDEFINIDO" -> 20;
                case "TEMPORAL"   -> 10;
                case "OBRA"       -> 5;
                default           -> 0;
            };
        }
        score += puntosContrato;

        // Factor 5: Coherencia documental completa (15 pts)
        boolean coherenciaCompleta = coherence.nombreConsistente()
            && coherence.cifConsistente()
            && coherence.secuenciaValida()
            && coherence.estabilidadSalarial()
            && coherence.documentosCompletos();
        if (coherenciaCompleta) score += 15;

        score = Math.min(100, Math.max(0, score));

        String level = calcularLevel(score);

        List<CheckResult> allChecks = new ArrayList<>(coherence.checks());
        allChecks.add(new CheckResult("Tipo de contrato", puntosContrato > 0,
            contractType != null ? "Tipo detectado: " + contractType + " (+" + puntosContrato + " pts)"
                : "No se detectó tipo de contrato."));
        allChecks.add(new CheckResult("Coherencia documental completa", coherenciaCompleta,
            coherenciaCompleta ? "Todos los checks superados (+15 pts)."
                : "Algún check falló — coherencia parcial."));

        return new SolvencyAnalysis(score, level, allChecks, coherence.documentosCompletos(), contractType);
    }

    // --- Privados ---

    private String extractNombre(String text) {
        Matcher m = NOMBRE_PATTERN.matcher(text);
        return m.find() ? m.group(1).trim() : null;
    }

    private String extractCif(String text) {
        Matcher m = CIF_PATTERN.matcher(text);
        return m.find() ? m.group(1) : null;
    }

    private YearMonth extractFecha(String text) {
        Matcher m = FECHA_NOMINA_PATTERN.matcher(text.toLowerCase());
        if (!m.find()) return null;
        String mesStr = m.group(0).toLowerCase().split("\\s")[0];
        Integer mes = MONTH_MAP.get(mesStr);
        if (mes == null) return null;
        int anio = Integer.parseInt(m.group(1));
        return YearMonth.of(anio, mes);
    }

    private BigDecimal extractImporte(String text, Pattern pattern) {
        Matcher m = pattern.matcher(text);
        if (!m.find()) return null;
        String raw = m.group(1).replace(".", "").replace(",", ".");
        try {
            return new BigDecimal(raw).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String extractTipoContrato(String text) {
        if (CONTRATO_INDEFINIDO_PATTERN.matcher(text).find()) return "INDEFINIDO";
        if (CONTRATO_TEMPORAL_PATTERN.matcher(text).find()) return "TEMPORAL";
        return null;
    }

    private boolean checkNombreConsistente(List<DocumentFields> docs) {
        List<String> nombres = docs.stream()
            .map(DocumentFields::nombre)
            .filter(Objects::nonNull)
            .map(String::toLowerCase)
            .distinct()
            .toList();
        return nombres.size() <= 1;
    }

    private boolean checkCifConsistente(List<DocumentFields> docs) {
        List<String> cifs = docs.stream()
            .map(DocumentFields::cif)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
        return cifs.size() <= 1;
    }

    private boolean checkSecuenciaValida(List<DocumentFields> nominas) {
        if (nominas.size() < 3) return false;
        List<YearMonth> fechas = nominas.stream()
            .map(DocumentFields::fecha)
            .filter(Objects::nonNull)
            .sorted()
            .toList();
        if (fechas.size() < 3) return false;
        for (int i = 1; i < fechas.size(); i++) {
            if (!fechas.get(i).equals(fechas.get(i - 1).plusMonths(1))) return false;
        }
        return true;
    }

    private boolean checkEstabilidadSalarial(List<DocumentFields> nominas) {
        List<BigDecimal> salarios = nominas.stream()
            .map(DocumentFields::salarioNeto)
            .filter(Objects::nonNull)
            .toList();
        if (salarios.size() < 2) return true; // no hay suficientes para comparar
        BigDecimal max = salarios.stream().max(Comparator.naturalOrder()).get();
        BigDecimal min = salarios.stream().min(Comparator.naturalOrder()).get();
        if (min.compareTo(BigDecimal.ZERO) == 0) return false;
        BigDecimal variacion = max.subtract(min).divide(min, 4, RoundingMode.HALF_UP);
        return variacion.compareTo(new BigDecimal("0.20")) < 0;
    }

    private int calcularPuntosNivelSalarial(List<DocumentFields> nominas) {
        if (nominas.isEmpty()) return 0;
        OptionalDouble avg = nominas.stream()
            .map(DocumentFields::salarioNeto)
            .filter(Objects::nonNull)
            .mapToDouble(BigDecimal::doubleValue)
            .average();
        if (avg.isEmpty()) return 0;
        double salarioMedio = avg.getAsDouble();
        if (salarioMedio >= 3000) return 25;
        if (salarioMedio >= 2000) return 18;
        if (salarioMedio >= 1500) return 12;
        if (salarioMedio >= 1000) return 6;
        return 0;
    }

    private int calcularPuntosAntiguedad(DocumentFields contrato) {
        if (contrato == null || contrato.fecha() == null) return 0;
        YearMonth inicio = contrato.fecha();
        long meses = inicio.until(YearMonth.now(), java.time.temporal.ChronoUnit.MONTHS);
        if (meses >= 60) return 20; // 5+ años
        if (meses >= 36) return 15; // 3+ años
        if (meses >= 12) return 10; // 1+ año
        if (meses >= 6)  return 5;
        return 0;
    }

    private String calcularLevel(int score) {
        if (score >= 80) return "PLATINUM";
        if (score >= 60) return "GOLD";
        if (score >= 40) return "SILVER";
        return "BRONZE";
    }
}
