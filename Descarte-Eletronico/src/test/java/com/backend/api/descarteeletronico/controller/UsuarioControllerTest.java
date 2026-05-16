package com.backend.api.descarteeletronico.controller;

import com.backend.api.descarteeletronico.exception.BusinessException;
import com.backend.api.descarteeletronico.exception.GlobalExceptionHandler;
import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioResponse;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioUpdateRequest;
import com.backend.api.descarteeletronico.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UsuarioControllerTest {

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private UsuarioService usuarioService;
  private UsuarioResponse response;

  @BeforeEach
  void setUp() {
    usuarioService = mock(UsuarioService.class);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new UsuarioController(usuarioService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    objectMapper = new ObjectMapper();

    response =
        new UsuarioResponse(
            UUID.randomUUID(), "Administrador", "admin@descarte.local", Set.of(), EntityStatus.ACTIVE);
  }

  @Test
  void findMeReturnsOkResponse() throws Exception {
    when(usuarioService.findMe()).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/usuarios/me"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(response.email()))
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
        .andExpect(jsonPath("$.message").value("Usuário administrador não encontrado"));

    verify(usuarioService).findMe();
    verifyNoMoreInteractions(usuarioService);
  }

  @Test
  void updateMeReturnsOkResponse() throws Exception {
    UsuarioUpdateRequest request =
        new UsuarioUpdateRequest("Admin Atualizado", "novo@descarte.local", "Admin@456");
    when(usuarioService.updateMe(request)).thenReturn(response);

    mockMvc
        .perform(
            patch("/api/v1/usuarios/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(response.email()));

    verify(usuarioService).updateMe(request);
    verifyNoMoreInteractions(usuarioService);
  }

  @Test
  void updateMeReturnsBadRequestWhenPayloadIsInvalid() throws Exception {
    UsuarioUpdateRequest request =
        new UsuarioUpdateRequest("A".repeat(101), "email-invalido", "curta");

    mockMvc
        .perform(
            patch("/api/v1/usuarios/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"));

    verifyNoInteractions(usuarioService);
  }

  @Test
  void updateMeReturnsBadRequestWhenNoFieldWasProvided() throws Exception {
    UsuarioUpdateRequest request = new UsuarioUpdateRequest(null, null, null);
    when(usuarioService.updateMe(request))
        .thenThrow(new BusinessException("Informe ao menos um campo para atualização."));

    mockMvc
        .perform(
            patch("/api/v1/usuarios/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Informe ao menos um campo para atualização."));

    verify(usuarioService).updateMe(request);
    verifyNoMoreInteractions(usuarioService);
  }
}
