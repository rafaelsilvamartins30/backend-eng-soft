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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints para gerenciamento de administradores")
public class UsuarioController {

    private final UsuarioService service;

    @Operation(summary = "Criar um novo usuario admin", description = "Cria um novo administrador no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario criado", content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<UsuarioResponse> create(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Listar todos os usuarios ativos")
    @ApiResponse(responseCode = "200", description = "Lista recuperada")
    @GetMapping
    public ResponseEntity<Set<UsuarioResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Buscar usuario por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> findById(
            @Parameter(description = "ID único do usuário") @PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Atualizar dados do usuario")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> update(
            @Parameter(description = "ID do usuário a ser editado") @PathVariable UUID id,
            @Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(summary = "Deletar usuario (Soft Delete)")
    @ApiResponse(responseCode = "204", description = "Usuario deletado com sucesso")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do usuário a ser removido") @PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}