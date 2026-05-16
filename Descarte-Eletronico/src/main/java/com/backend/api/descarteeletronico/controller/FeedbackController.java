package com.backend.api.descarteeletronico.controller;

import com.backend.api.descarteeletronico.exception.ErrorResponseDTO;
import com.backend.api.descarteeletronico.model.feedback.dto.FeedbackResponse;
import com.backend.api.descarteeletronico.service.FeedbackService;
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
@RequestMapping("/api/v1/feedbacks")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Feedbacks", description = "Gestão administrativa dos feedbacks recebidos")
public class FeedbackController {

  private final FeedbackService feedbackService;

  @Operation(summary = "Lista feedbacks administrativos protegidos por role ADMIN")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Feedbacks listados",
        content = @Content(schema = @Schema(implementation = FeedbackResponse.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @GetMapping
  public ResponseEntity<Set<FeedbackResponse>> findAll() {
    return ResponseEntity.ok(feedbackService.findAll());
  }

  @Operation(summary = "Lista feedbacks não vistos administrativos protegidos por role ADMIN")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Feedbacks não vistos listados",
        content = @Content(schema = @Schema(implementation = FeedbackResponse.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @GetMapping("/nao-visualizados")
  public ResponseEntity<Set<FeedbackResponse>> findUnread() {
    return ResponseEntity.ok(feedbackService.findUnread());
  }

  @Operation(summary = "Busca feedback administrativo protegido por role ADMIN")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Feedback encontrado",
        content = @Content(schema = @Schema(implementation = FeedbackResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Feedback não encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @GetMapping("/{id}")
  public ResponseEntity<FeedbackResponse> findById(
      @Parameter(description = "ID do feedback") @PathVariable UUID id) {
    return ResponseEntity.ok(feedbackService.findById(id));
  }

  @Operation(summary = "Marca feedback como visto administrativo protegido por role ADMIN")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Feedback marcado como visto",
        content = @Content(schema = @Schema(implementation = FeedbackResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Feedback não encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @PatchMapping("/{id}/visualizar")
  public ResponseEntity<FeedbackResponse> markAsViewed(
      @Parameter(description = "ID do feedback") @PathVariable UUID id) {
    return ResponseEntity.ok(feedbackService.markAsViewed(id));
  }

  @Operation(summary = "Remove feedback administrativo protegido por role ADMIN")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Feedback removido"),
    @ApiResponse(
        responseCode = "404",
        description = "Feedback não encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno inesperado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @Parameter(description = "ID do feedback") @PathVariable UUID id) {
    feedbackService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
