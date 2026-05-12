package com.backend.api.descarteeletronico.controller;

import com.backend.api.descarteeletronico.exception.ErrorResponseDTO;
import com.backend.api.descarteeletronico.model.tipoproduto.dto.TipoProdutoRequest;
import com.backend.api.descarteeletronico.model.tipoproduto.dto.TipoProdutoResponse;
import com.backend.api.descarteeletronico.service.TipoProdutoService;
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
@RequestMapping("/api/v1/tipos-produto")
@RequiredArgsConstructor
@Tag(name = "Tipos de Produto", description = "CRUD dos tipos de produto aceitos nos pontos")
public class TipoProdutoController {

  private final TipoProdutoService tipoProdutoService;

  @Operation(summary = "Cria um tipo de produto")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Tipo de produto criado",
        content = @Content(schema = @Schema(implementation = TipoProdutoResponse.class))),
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
  public ResponseEntity<TipoProdutoResponse> create(
      @Valid @RequestBody TipoProdutoRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(tipoProdutoService.create(request));
  }

  @Operation(summary = "Busca um tipo de produto por ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Tipo de produto encontrado",
        content = @Content(schema = @Schema(implementation = TipoProdutoResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Tipo de produto não encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @GetMapping("/{id}")
  public ResponseEntity<TipoProdutoResponse> findById(
      @Parameter(description = "ID do tipo de produto") @PathVariable UUID id) {
    return ResponseEntity.ok(tipoProdutoService.findById(id));
  }

  @Operation(summary = "Lista tipos de produto ativos")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Tipos de produto listados",
        content = @Content(schema = @Schema(implementation = TipoProdutoResponse.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @GetMapping
  public ResponseEntity<Set<TipoProdutoResponse>> findAll() {
    return ResponseEntity.ok(tipoProdutoService.findAll());
  }

  @Operation(summary = "Atualiza um tipo de produto")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Tipo de produto atualizado",
        content = @Content(schema = @Schema(implementation = TipoProdutoResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Dados inválidos ou regra de negócio violada",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Tipo de produto não encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @PutMapping("/{id}")
  public ResponseEntity<TipoProdutoResponse> update(
      @Parameter(description = "ID do tipo de produto") @PathVariable UUID id,
      @Valid @RequestBody TipoProdutoRequest request) {
    return ResponseEntity.ok(tipoProdutoService.update(id, request));
  }

  @Operation(summary = "Remove um tipo de produto com soft delete")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Tipo de produto removido"),
    @ApiResponse(
        responseCode = "404",
        description = "Tipo de produto não encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @Parameter(description = "ID do tipo de produto") @PathVariable UUID id) {
    tipoProdutoService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
