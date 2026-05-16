package com.backend.api.descarteeletronico.controller;

import com.backend.api.descarteeletronico.exception.ErrorResponseDTO;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioResponse;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioUpdateRequest;
import com.backend.api.descarteeletronico.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Usuário Admin", description = "Endpoints do único usuário administrador da aplicação")
public class UsuarioController {

  private final UsuarioService usuarioService;

  @Operation(summary = "Busca o usuário administrador atual")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Usuário administrador encontrado",
        content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Usuário administrador não encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @GetMapping("/me")
  public ResponseEntity<UsuarioResponse> findMe() {
    return ResponseEntity.ok(usuarioService.findMe());
  }

  @Operation(summary = "Atualiza dados do usuário administrador atual")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Usuário administrador atualizado",
        content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Dados inválidos ou regra de negócio violada",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Usuário administrador não encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @PatchMapping("/me")
  public ResponseEntity<UsuarioResponse> updateMe(
      @Valid @RequestBody UsuarioUpdateRequest request) {
    return ResponseEntity.ok(usuarioService.updateMe(request));
  }
}
