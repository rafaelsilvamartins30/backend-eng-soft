package com.backend.api.descarteeletronico.controller;

import com.backend.api.descarteeletronico.exception.ErrorResponseDTO;
import com.backend.api.descarteeletronico.model.pontocoleta.dto.PontoColetaRequest;
import com.backend.api.descarteeletronico.model.pontocoleta.dto.PontoColetaResponse;
import com.backend.api.descarteeletronico.service.PontoColetaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pontos-coleta")
@RequiredArgsConstructor
@Tag(name = "Pontos de Coleta", description = "CRUD dos pontos de coleta de eletrônicos")
public class PontoColetaController {

  private final PontoColetaService pontoColetaService;

  @Operation(summary = "Cria um ponto de coleta")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Ponto de coleta criado",
        content = @Content(schema = @Schema(implementation = PontoColetaResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Dados inválidos ou regra de negócio violada",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @PostMapping
  public ResponseEntity<PontoColetaResponse> create(
      @Valid @RequestBody PontoColetaRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(pontoColetaService.create(request));
  }

  @Operation(summary = "Busca um ponto de coleta por ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Ponto de coleta encontrado",
        content = @Content(schema = @Schema(implementation = PontoColetaResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Ponto de coleta não encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @GetMapping("/{id}")
  public ResponseEntity<PontoColetaResponse> findById(
      @Parameter(description = "ID do ponto de coleta") @PathVariable UUID id) {
    return ResponseEntity.ok(pontoColetaService.findById(id));
  }

  @Operation(summary = "Lista pontos de coleta ativos")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Pontos de coleta listados",
        content = @Content(schema = @Schema(implementation = PontoColetaResponse.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @GetMapping
  public ResponseEntity<Set<PontoColetaResponse>> findAll() {
    return ResponseEntity.ok(pontoColetaService.findAll());
  }

  @Operation(summary = "Atualiza um ponto de coleta")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Ponto de coleta atualizado",
        content = @Content(schema = @Schema(implementation = PontoColetaResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Dados inválidos ou regra de negócio violada",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Ponto de coleta não encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @PutMapping("/{id}")
  public ResponseEntity<PontoColetaResponse> update(
      @Parameter(description = "ID do ponto de coleta") @PathVariable UUID id,
      @Valid @RequestBody PontoColetaRequest request) {
    return ResponseEntity.ok(pontoColetaService.update(id, request));
  }

  @Operation(summary = "Remove um ponto de coleta com soft delete")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Ponto de coleta removido"),
    @ApiResponse(
        responseCode = "404",
        description = "Ponto de coleta não encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @Parameter(description = "ID do ponto de coleta") @PathVariable UUID id) {
    pontoColetaService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
