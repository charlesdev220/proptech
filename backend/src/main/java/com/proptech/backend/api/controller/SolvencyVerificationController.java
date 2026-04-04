package com.proptech.backend.api.controller;

import com.proptech.backend.api.dto.SolvencyCheckDTO;
import com.proptech.backend.api.dto.SolvencyResultDTO;
import com.proptech.backend.domain.service.PdfExtractorService;
import com.proptech.backend.domain.service.SolvencyAnalyzerService;
import com.proptech.backend.infrastructure.persistence.entity.UserEntity;
import com.proptech.backend.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class SolvencyVerificationController {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024L;
    private static final Set<String> ALLOWED_TYPES = Set.of("application/pdf");

    private final PdfExtractorService pdfExtractorService;
    private final SolvencyAnalyzerService solvencyAnalyzerService;
    private final UserRepository userRepository;

    @PostMapping("/solvency-verification")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SolvencyResultDTO> verifySolvency(
            @RequestParam("files") List<MultipartFile> files,
            @AuthenticationPrincipal UserEntity authenticatedUser) throws IOException {

        for (MultipartFile file : files) {
            if (!ALLOWED_TYPES.contains(file.getContentType())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tipo de archivo no permitido. Solo se aceptan PDFs.");
            }
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El archivo '" + file.getOriginalFilename() + "' supera el límite de 5MB.");
            }
        }

        List<SolvencyAnalyzerService.DocumentFields> extractedDocs = files.stream()
            .map(file -> {
                try {
                    PdfExtractorService.ExtractionResult result =
                        pdfExtractorService.extract(file.getInputStream());
                    if (!result.readable()) {
                        return new SolvencyAnalyzerService.DocumentFields(
                            null, null, null, null, null, null, false);
                    }
                    return solvencyAnalyzerService.extractFields(result.text());
                } catch (IOException e) {
                    return new SolvencyAnalyzerService.DocumentFields(
                        null, null, null, null, null, null, false);
                }
            })
            .toList();

        SolvencyAnalyzerService.CoherenceResult coherence =
            solvencyAnalyzerService.validateCoherence(extractedDocs);
        SolvencyAnalyzerService.SolvencyAnalysis analysis =
            solvencyAnalyzerService.calculateScore(coherence);

        UserEntity user = userRepository.findById(authenticatedUser.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        user.setSolvencyScore(analysis.score());
        user.setSolvencyVerifiedAt(LocalDateTime.now());
        user.setSolvencyContractType(analysis.contractType());
        userRepository.save(user);

        SolvencyResultDTO dto = new SolvencyResultDTO();
        dto.setScore(analysis.score());
        dto.setLevel(SolvencyResultDTO.LevelEnum.fromValue(analysis.level()));
        dto.setVerifiedAt(OffsetDateTime.now(ZoneOffset.UTC));
        dto.setContractType(analysis.contractType() != null
            ? SolvencyResultDTO.ContractTypeEnum.fromValue(analysis.contractType())
            : null);
        dto.setDocumentosCompletos(analysis.documentosCompletos());
        dto.setChecks(analysis.checks().stream()
            .map(c -> {
                SolvencyCheckDTO check = new SolvencyCheckDTO();
                check.setName(c.name());
                check.setPassed(c.passed());
                check.setDescription(c.description());
                return check;
            }).toList());

        return ResponseEntity.ok(dto);
    }
}
