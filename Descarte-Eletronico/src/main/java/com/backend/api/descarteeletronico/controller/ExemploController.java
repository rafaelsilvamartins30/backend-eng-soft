package com.backend.api.descarteeletronico.controller;

import com.backend.api.descarteeletronico.exception.ErrorResponseDTO;
import com.backend.api.descarteeletronico.model.exemplo.dto.ExemploRequest;
import com.backend.api.descarteeletronico.model.exemplo.dto.ExemploResponse;
import com.backend.api.descarteeletronico.service.ExemploService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
@RequestMapping("/api/v1/exemplos")
@RequiredArgsConstructor
@Tag(name = "Exemplos", description = "CRUD de referência para novas entidades")
public class ExemploController {

  private final ExemploService exemploService;

  @Operation(
      summary = "Cria um exemplo",
      description =
          "Cria um registro ativo de exemplo. Use este endpoint como referência para novos controllers.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Exemplo criado",
        content = @Content(schema = @Schema(implementation = ExemploResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Dados inválidos ou regra de negócio violada",
        content =
            @Content(
                schema = @Schema(implementation = ErrorResponseDTO.class),
                examples =
                    @ExampleObject(
                        value =
                            """
                                    {
                                      "timestamp": "2026-05-04T21:30:00",
                                      "status": 400,
                                      "error": "Bad Request",
                                      "message": "Dados de entrada inválidos",
                                      "path": "/api/v1/exemplos",
                                      "details": ["nome: O nome é obrigatório"]
                                    }
                                    """))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @PostMapping
  public ResponseEntity<ExemploResponse> create(@Valid @RequestBody ExemploRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(exemploService.create(request));
  }

  @Operation(summary = "Busca um exemplo por ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Exemplo encontrado",
        content = @Content(schema = @Schema(implementation = ExemploResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Exemplo não encontrado",
        content =
            @Content(
                schema = @Schema(implementation = ErrorResponseDTO.class),
                examples =
                    @ExampleObject(
                        value =
                            """
                                    {
                                      "timestamp": "2026-05-04T21:30:00",
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "Exemplo não encontrado",
                                      "path": "/api/v1/exemplos/4fbb2c8e-8737-4e24-9ef0-0db72a231ce8",
                                      "details": []
                                    }
                                    """))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @GetMapping("/{id}")
  public ResponseEntity<ExemploResponse> findById(
      @Parameter(description = "ID do exemplo") @PathVariable UUID id) {
    return ResponseEntity.ok(exemploService.findById(id));
  }

  @Operation(summary = "Lista exemplos ativos")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Exemplos listados",
        content = @Content(schema = @Schema(implementation = ExemploResponse.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @GetMapping
  public ResponseEntity<Set<ExemploResponse>> findAll() {
    return ResponseEntity.ok(exemploService.findAll());
  }

  @Operation(summary = "Atualiza um exemplo")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Exemplo atualizado",
        content = @Content(schema = @Schema(implementation = ExemploResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Dados inválidos ou regra de negócio violada",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Exemplo não encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @PutMapping("/{id}")
  public ResponseEntity<ExemploResponse> update(
      @Parameter(description = "ID do exemplo") @PathVariable UUID id,
      @Valid @RequestBody ExemploRequest request) {
    return ResponseEntity.ok(exemploService.update(id, request));
  }

  @Operation(summary = "Remove um exemplo com soft delete")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Exemplo removido"),
    @ApiResponse(
        responseCode = "404",
        description = "Exemplo não encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @Parameter(description = "ID do exemplo") @PathVariable UUID id) {
    exemploService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
