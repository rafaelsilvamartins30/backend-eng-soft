package com.backend.api.descarteeletronico.controller;

import com.backend.api.descarteeletronico.exception.ErrorResponseDTO;
import com.backend.api.descarteeletronico.model.notificacao.dto.NotificacaoResponse;
import com.backend.api.descarteeletronico.service.NotificacaoService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notificacoes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Notificações", description = "Gestão administrativa das notificações")
public class NotificacaoController {

  private final NotificacaoService notificacaoService;

  @Operation(summary = "Lista notificações administrativas protegidas por role ADMIN")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Notificações listadas",
        content = @Content(schema = @Schema(implementation = NotificacaoResponse.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @GetMapping
  public ResponseEntity<Set<NotificacaoResponse>> findAll() {
    return ResponseEntity.ok(notificacaoService.findAll());
  }

  @Operation(summary = "Lista notificações não vistas administrativas protegidas por role ADMIN")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Notificações não vistas listadas",
        content = @Content(schema = @Schema(implementation = NotificacaoResponse.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @GetMapping("/nao-visualizadas")
  public ResponseEntity<Set<NotificacaoResponse>> findUnread() {
    return ResponseEntity.ok(notificacaoService.findUnread());
  }

  @Operation(summary = "Marca notificação como vista administrativa protegida por role ADMIN")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Notificação marcada como vista",
        content = @Content(schema = @Schema(implementation = NotificacaoResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Notificação não encontrada",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @PatchMapping("/{id}/visualizar")
  public ResponseEntity<NotificacaoResponse> markAsViewed(
      @Parameter(description = "ID da notificação") @PathVariable UUID id) {
    return ResponseEntity.ok(notificacaoService.markAsViewed(id));
  }

  @Operation(summary = "Remove notificação administrativa protegida por role ADMIN")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Notificação removida"),
    @ApiResponse(
        responseCode = "404",
        description = "Notificação não encontrada",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @Parameter(description = "ID da notificação") @PathVariable UUID id) {
    notificacaoService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
