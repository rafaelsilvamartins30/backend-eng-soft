package com.backend.api.descarteeletronico.controller;

import com.backend.api.descarteeletronico.exception.ErrorResponseDTO;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioRequest;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioResponse;
import com.backend.api.descarteeletronico.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Set;
import java.util.UUID;
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

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "CRUD de usuários administradores")
public class UsuarioController {

  private final UsuarioService usuarioService;

  @Operation(summary = "Cria um usuário administrador")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Usuário criado",
        content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
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
  public ResponseEntity<UsuarioResponse> create(@Valid @RequestBody UsuarioRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.create(request));
  }

  @Operation(summary = "Busca um usuário administrador por ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Usuário encontrado",
        content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Usuário não encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @GetMapping("/{id}")
  public ResponseEntity<UsuarioResponse> findById(
      @Parameter(description = "ID do usuário administrador") @PathVariable UUID id) {
    return ResponseEntity.ok(usuarioService.findById(id));
  }

  @Operation(summary = "Lista usuários administradores ativos")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Usuários listados",
        content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @GetMapping
  public ResponseEntity<Set<UsuarioResponse>> findAll() {
    return ResponseEntity.ok(usuarioService.findAll());
  }

  @Operation(summary = "Atualiza um usuário administrador")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Usuário atualizado",
        content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Dados inválidos ou regra de negócio violada",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Usuário não encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @PutMapping("/{id}")
  public ResponseEntity<UsuarioResponse> update(
      @Parameter(description = "ID do usuário administrador") @PathVariable UUID id,
      @Valid @RequestBody UsuarioRequest request) {
    return ResponseEntity.ok(usuarioService.update(id, request));
  }

  @Operation(summary = "Remove um usuário administrador com soft delete")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Usuário removido"),
    @ApiResponse(
        responseCode = "404",
        description = "Usuário não encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @Parameter(description = "ID do usuário administrador") @PathVariable UUID id) {
    usuarioService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
