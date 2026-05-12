package com.backend.api.descarteeletronico.controller;

import com.backend.api.descarteeletronico.exception.BusinessException;
import com.backend.api.descarteeletronico.exception.GlobalExceptionHandler;
import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.tipoproduto.dto.TipoProdutoRequest;
import com.backend.api.descarteeletronico.model.tipoproduto.dto.TipoProdutoResponse;
import com.backend.api.descarteeletronico.service.TipoProdutoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;
import java.util.UUID;

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

class TipoProdutoControllerTest {

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private TipoProdutoService tipoProdutoService;
  private UUID id;
  private TipoProdutoRequest request;
  private TipoProdutoResponse response;

  @BeforeEach
  void setUp() {
    tipoProdutoService = mock(TipoProdutoService.class);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new TipoProdutoController(tipoProdutoService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    objectMapper = new ObjectMapper();

    id = UUID.randomUUID();
    request = new TipoProdutoRequest("Computadores", "Notebooks, desktops e monitores");
    response =
        new TipoProdutoResponse(
            id, request.nome(), request.descricaoExemplos(), 0L, null, null, EntityStatus.ACTIVE, null);
  }

  @Test
  void createReturnsCreatedResponse() throws Exception {
    when(tipoProdutoService.create(request)).thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/tipos-produto")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.nome").value(request.nome()));

    verify(tipoProdutoService).create(request);
    verifyNoMoreInteractions(tipoProdutoService);
  }

  @Test
  void createReturnsBadRequestWhenPayloadIsInvalid() throws Exception {
    TipoProdutoRequest invalidRequest = new TipoProdutoRequest("", "");

    mockMvc
        .perform(
            post("/api/v1/tipos-produto")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"));

    verifyNoInteractions(tipoProdutoService);
  }

  @Test
  void createReturnsBadRequestWhenPayloadIsTooLarge() throws Exception {
    TipoProdutoRequest invalidRequest = new TipoProdutoRequest("A".repeat(101), "B".repeat(501));

    mockMvc
        .perform(
            post("/api/v1/tipos-produto")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.details").isArray());

    verifyNoInteractions(tipoProdutoService);
  }

  @Test
  void createReturnsBadRequestWhenServiceThrowsBusinessException() throws Exception {
    when(tipoProdutoService.create(request)).thenThrow(new BusinessException("Regra inválida"));

    mockMvc
        .perform(
            post("/api/v1/tipos-produto")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Regra inválida"))
        .andExpect(jsonPath("$.path").value("/api/v1/tipos-produto"));

    verify(tipoProdutoService).create(request);
    verifyNoMoreInteractions(tipoProdutoService);
  }

  @Test
  void findByIdReturnsOkResponse() throws Exception {
    when(tipoProdutoService.findById(id)).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/tipos-produto/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()));

    verify(tipoProdutoService).findById(id);
    verifyNoMoreInteractions(tipoProdutoService);
  }

  @Test
  void findByIdReturnsNotFoundWhenServiceThrows() throws Exception {
    when(tipoProdutoService.findById(id))
        .thenThrow(new ResourceNotFoundException("Tipo de produto não encontrado"));

    mockMvc
        .perform(get("/api/v1/tipos-produto/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Tipo de produto não encontrado"));

    verify(tipoProdutoService).findById(id);
    verifyNoMoreInteractions(tipoProdutoService);
  }

  @Test
  void findAllReturnsOkResponse() throws Exception {
    when(tipoProdutoService.findAll()).thenReturn(Set.of(response));

    mockMvc
        .perform(get("/api/v1/tipos-produto"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(id.toString()));

    verify(tipoProdutoService).findAll();
    verifyNoMoreInteractions(tipoProdutoService);
  }

  @Test
  void findAllReturnsEmptyListWhenThereAreNoActiveEntities() throws Exception {
    when(tipoProdutoService.findAll()).thenReturn(Set.of());

    mockMvc
        .perform(get("/api/v1/tipos-produto"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());

    verify(tipoProdutoService).findAll();
    verifyNoMoreInteractions(tipoProdutoService);
  }

  @Test
  void findAllReturnsInternalServerErrorWhenServiceThrowsUnexpectedException() throws Exception {
    when(tipoProdutoService.findAll()).thenThrow(new IllegalStateException("Falha inesperada"));

    mockMvc
        .perform(get("/api/v1/tipos-produto"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value("Erro interno inesperado"))
        .andExpect(jsonPath("$.path").value("/api/v1/tipos-produto"));

    verify(tipoProdutoService).findAll();
    verifyNoMoreInteractions(tipoProdutoService);
  }

  @Test
  void updateReturnsOkResponse() throws Exception {
    when(tipoProdutoService.update(id, request)).thenReturn(response);

    mockMvc
        .perform(
            put("/api/v1/tipos-produto/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()));

    verify(tipoProdutoService).update(id, request);
    verifyNoMoreInteractions(tipoProdutoService);
  }

  @Test
  void updateReturnsBadRequestWhenPayloadIsInvalid() throws Exception {
    TipoProdutoRequest invalidRequest = new TipoProdutoRequest("", "");

    mockMvc
        .perform(
            put("/api/v1/tipos-produto/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"));

    verifyNoInteractions(tipoProdutoService);
  }

  @Test
  void updateReturnsNotFoundWhenServiceThrows() throws Exception {
    when(tipoProdutoService.update(id, request))
        .thenThrow(new ResourceNotFoundException("Tipo de produto não encontrado"));

    mockMvc
        .perform(
            put("/api/v1/tipos-produto/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Tipo de produto não encontrado"));

    verify(tipoProdutoService).update(id, request);
    verifyNoMoreInteractions(tipoProdutoService);
  }

  @Test
  void deleteReturnsNoContent() throws Exception {
    mockMvc.perform(delete("/api/v1/tipos-produto/{id}", id)).andExpect(status().isNoContent());

    verify(tipoProdutoService).delete(id);
    verifyNoMoreInteractions(tipoProdutoService);
  }

  @Test
  void deleteReturnsNotFoundWhenServiceThrows() throws Exception {
    doThrow(new ResourceNotFoundException("Tipo de produto não encontrado"))
        .when(tipoProdutoService)
        .delete(id);

    mockMvc
        .perform(delete("/api/v1/tipos-produto/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Tipo de produto não encontrado"));

    verify(tipoProdutoService).delete(id);
    verifyNoMoreInteractions(tipoProdutoService);
  }
}
