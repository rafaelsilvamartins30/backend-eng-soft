package com.backend.api.descarteeletronico.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.api.descarteeletronico.exception.BusinessException;
import com.backend.api.descarteeletronico.exception.GlobalExceptionHandler;
import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioResponse;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioUpdateRequest;
import com.backend.api.descarteeletronico.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class UsuarioControllerTest {

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private UsuarioService usuarioService;
  private UUID id;
  private UsuarioUpdateRequest request;
  private UsuarioResponse response;

  @BeforeEach
  void setUp() {
    usuarioService = org.mockito.Mockito.mock(UsuarioService.class);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new UsuarioController(usuarioService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    objectMapper = new ObjectMapper();

    id = UUID.randomUUID();
    request = new UsuarioUpdateRequest("Maria Silva", "maria@descarte.com", "SenhaForte123");
    response =
        new UsuarioResponse(
            id, request.nome(), request.email(), 0L, null, null, EntityStatus.ACTIVE, null);
  }

  @Test
  void findMeReturnsOkResponse() throws Exception {
    when(usuarioService.findMe()).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/usuarios/me"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.nome").value(request.nome()))
        .andExpect(jsonPath("$.email").value(request.email()))
        .andExpect(jsonPath("$.senha").doesNotExist());

    verify(usuarioService).findMe();
    verifyNoMoreInteractions(usuarioService);
  }

  @Test
  void findMeReturnsNotFoundWhenAdminUserDoesNotExist() throws Exception {
    when(usuarioService.findMe())
        .thenThrow(new ResourceNotFoundException("Usuário administrador não encontrado"));

    mockMvc
        .perform(get("/api/v1/usuarios/me"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Usuário administrador não encontrado"))
        .andExpect(jsonPath("$.path").value("/api/v1/usuarios/me"));

    verify(usuarioService).findMe();
    verifyNoMoreInteractions(usuarioService);
  }

  @Test
  void updateMeReturnsOkResponse() throws Exception {
    when(usuarioService.updateMe(request)).thenReturn(response);

    mockMvc
        .perform(
            patch("/api/v1/usuarios/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.nome").value(request.nome()))
        .andExpect(jsonPath("$.email").value(request.email()))
        .andExpect(jsonPath("$.senha").doesNotExist());

    verify(usuarioService).updateMe(request);
    verifyNoMoreInteractions(usuarioService);
  }

  @Test
  void updateMeReturnsBadRequestWhenPayloadIsInvalid() throws Exception {
    UsuarioUpdateRequest invalidRequest =
        new UsuarioUpdateRequest("A".repeat(101), "email-invalido", "curta");

    mockMvc
        .perform(
            patch("/api/v1/usuarios/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"))
        .andExpect(jsonPath("$.details").isArray());

    verifyNoInteractions(usuarioService);
  }

  @Test
  void updateMeReturnsBadRequestWhenServiceThrowsBusinessException() throws Exception {
    when(usuarioService.updateMe(request))
        .thenThrow(new BusinessException("Informe ao menos um campo para atualização."));

    mockMvc
        .perform(
            patch("/api/v1/usuarios/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Informe ao menos um campo para atualização."))
        .andExpect(jsonPath("$.path").value("/api/v1/usuarios/me"));

    verify(usuarioService).updateMe(request);
    verifyNoMoreInteractions(usuarioService);
  }

  @Test
  void updateMeReturnsNotFoundWhenAdminUserDoesNotExist() throws Exception {
    when(usuarioService.updateMe(request))
        .thenThrow(new ResourceNotFoundException("Usuário administrador não encontrado"));

    mockMvc
        .perform(
            patch("/api/v1/usuarios/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Usuário administrador não encontrado"));

    verify(usuarioService).updateMe(request);
    verifyNoMoreInteractions(usuarioService);
  }
}
