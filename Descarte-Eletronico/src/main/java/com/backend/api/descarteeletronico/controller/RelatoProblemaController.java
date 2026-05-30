package com.backend.api.descarteeletronico.controller;

import com.backend.api.descarteeletronico.exception.ErrorResponseDTO;
import com.backend.api.descarteeletronico.model.relato.dto.RelatoProblemaResponse;
import com.backend.api.descarteeletronico.service.RelatoProblemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/relatos-problema")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Relatos de Problema", description = "Gestão administrativa dos relatos recebidos")
public class RelatoProblemaController {

    private final RelatoProblemaService relatoProblemaService;

    @Operation(summary = "Lista relatos administrativos protegidos por role ADMIN")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Relatos listados",
                    content = @Content(schema = @Schema(implementation = RelatoProblemaResponse.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<Set<RelatoProblemaResponse>> findAll() {
        return ResponseEntity.ok(relatoProblemaService.findAll());
    }

    @Operation(summary = "Busca relato administrativo protegido por role ADMIN")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Relato encontrado",
                    content = @Content(schema = @Schema(implementation = RelatoProblemaResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Relato não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<RelatoProblemaResponse> findById(
            @Parameter(description = "ID do relato") @PathVariable UUID id) {
        return ResponseEntity.ok(relatoProblemaService.findById(id));
    }

    @Operation(summary = "Remove relato administrativo protegido por role ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Relato removido"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Relato não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do relato") @PathVariable UUID id) {
        relatoProblemaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}