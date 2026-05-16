package com.backend.api.descarteeletronico.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.api.descarteeletronico.exception.BusinessException;
import com.backend.api.descarteeletronico.exception.GlobalExceptionHandler;
import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioRequest;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioResponse;
import com.backend.api.descarteeletronico.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
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
  private UsuarioRequest request;
  private UsuarioResponse response;

  @BeforeEach
  void setUp() {
    usuarioService = mock(UsuarioService.class);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new UsuarioController(usuarioService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    objectMapper = new ObjectMapper();

    id = UUID.randomUUID();
    request = new UsuarioRequest("Maria Silva", "maria@descarte.com", "SenhaForte123");
    response =
        new UsuarioResponse(
            id, request.nome(), request.email(), 0L, null, null, EntityStatus.ACTIVE, null);
  }

  @Test
  void createReturnsCreatedResponse() throws Exception {
    when(usuarioService.create(request)).thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.nome").value(request.nome()))
        .andExpect(jsonPath("$.email").value(request.email()))
        .andExpect(jsonPath("$.senha").doesNotExist());

    verify(usuarioService).create(request);
    verifyNoMoreInteractions(usuarioService);
  }

  @Test
  void createReturnsBadRequestWhenPayloadIsInvalid() throws Exception {
    UsuarioRequest invalidRequest = new UsuarioRequest("", "email-invalido", "");

    mockMvc
        .perform(
            post("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"));

    verifyNoInteractions(usuarioService);
  }

  @Test
  void createReturnsBadRequestWhenPayloadIsTooLarge() throws Exception {
    UsuarioRequest invalidRequest =
        new UsuarioRequest("A".repeat(101), "B".repeat(91) + "@email.com", "C".repeat(256));

    mockMvc
        .perform(
            post("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.details").isArray());

    verifyNoInteractions(usuarioService);
  }

  @Test
  void createReturnsBadRequestWhenServiceThrowsBusinessException() throws Exception {
    when(usuarioService.create(request))
        .thenThrow(new BusinessException("Já existe um usuário ativo cadastrado com este e-mail."));

    mockMvc
        .perform(
            post("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.message").value("Já existe um usuário ativo cadastrado com este e-mail."))
        .andExpect(jsonPath("$.path").value("/api/v1/usuarios"));

    verify(usuarioService).create(request);
    verifyNoMoreInteractions(usuarioService);
  }

  @Test
  void findByIdReturnsOkResponse() throws Exception {
    when(usuarioService.findById(id)).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/usuarios/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()));

    verify(usuarioService).findById(id);
    verifyNoMoreInteractions(usuarioService);
  }

  @Test
  void findByIdReturnsNotFoundWhenServiceThrows() throws Exception {
    when(usuarioService.findById(id))
        .thenThrow(new ResourceNotFoundException("Usuário não encontrado"));

    mockMvc
        .perform(get("/api/v1/usuarios/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Usuário não encontrado"));

    verify(usuarioService).findById(id);
    verifyNoMoreInteractions(usuarioService);
  }

  @Test
  void findAllReturnsOkResponse() throws Exception {
    when(usuarioService.findAll()).thenReturn(Set.of(response));

    mockMvc
        .perform(get("/api/v1/usuarios"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(id.toString()));

    verify(usuarioService).findAll();
    verifyNoMoreInteractions(usuarioService);
  }

  @Test
  void findAllReturnsEmptyListWhenThereAreNoActiveEntities() throws Exception {
    when(usuarioService.findAll()).thenReturn(Set.of());

    mockMvc
        .perform(get("/api/v1/usuarios"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());

    verify(usuarioService).findAll();
    verifyNoMoreInteractions(usuarioService);
  }

  @Test
  void findAllReturnsInternalServerErrorWhenServiceThrowsUnexpectedException() throws Exception {
    when(usuarioService.findAll()).thenThrow(new IllegalStateException("Falha inesperada"));

    mockMvc
        .perform(get("/api/v1/usuarios"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value("Erro interno inesperado"))
        .andExpect(jsonPath("$.path").value("/api/v1/usuarios"));

    verify(usuarioService).findAll();
    verifyNoMoreInteractions(usuarioService);
  }

  @Test
  void updateReturnsOkResponse() throws Exception {
    when(usuarioService.update(id, request)).thenReturn(response);

    mockMvc
        .perform(
            put("/api/v1/usuarios/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()));

    verify(usuarioService).update(id, request);
    verifyNoMoreInteractions(usuarioService);
  }

  @Test
  void updateReturnsBadRequestWhenPayloadIsInvalid() throws Exception {
    UsuarioRequest invalidRequest = new UsuarioRequest("", "email-invalido", "");

    mockMvc
        .perform(
            put("/api/v1/usuarios/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"));

    verifyNoInteractions(usuarioService);
  }

  @Test
  void updateReturnsNotFoundWhenServiceThrows() throws Exception {
    when(usuarioService.update(id, request))
        .thenThrow(new ResourceNotFoundException("Usuário não encontrado"));

    mockMvc
        .perform(
            put("/api/v1/usuarios/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Usuário não encontrado"));

    verify(usuarioService).update(id, request);
    verifyNoMoreInteractions(usuarioService);
  }

  @Test
  void deleteReturnsNoContent() throws Exception {
    mockMvc.perform(delete("/api/v1/usuarios/{id}", id)).andExpect(status().isNoContent());

    verify(usuarioService).delete(id);
    verifyNoMoreInteractions(usuarioService);
  }

  @Test
  void deleteReturnsNotFoundWhenServiceThrows() throws Exception {
    doThrow(new ResourceNotFoundException("Usuário não encontrado"))
        .when(usuarioService)
        .delete(id);

    mockMvc
        .perform(delete("/api/v1/usuarios/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Usuário não encontrado"));

    verify(usuarioService).delete(id);
    verifyNoMoreInteractions(usuarioService);
  }
}
