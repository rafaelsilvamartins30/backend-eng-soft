package com.backend.api.descarteeletronico.controller;

import com.backend.api.descarteeletronico.exception.GlobalExceptionHandler;
import com.backend.api.descarteeletronico.exception.BusinessException;
import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.exemplo.dto.ExemploRequest;
import com.backend.api.descarteeletronico.model.exemplo.dto.ExemploResponse;
import com.backend.api.descarteeletronico.service.ExemploService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
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

class ExemploControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ExemploService exemploService;
    private UUID id;
    private ExemploRequest request;
    private ExemploResponse response;

    @BeforeEach
    void setUp() {
        exemploService = mock(ExemploService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ExemploController(exemploService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        id = UUID.randomUUID();
        request = new ExemploRequest("Coleta de notebook", "Equipamento antigo para descarte");
        response = new ExemploResponse(id, request.nome(), request.descricao(), 0L, null, null, EntityStatus.ACTIVE, null);
    }

    @Test
    void createReturnsCreatedResponse() throws Exception {
        when(exemploService.create(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/exemplos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value(request.nome()))
                .andExpect(jsonPath("$.descricao").value(request.descricao()));

        verify(exemploService).create(request);
        verifyNoMoreInteractions(exemploService);
    }

    @Test
    void createReturnsBadRequestWhenPayloadIsInvalid() throws Exception {
        ExemploRequest invalidRequest = new ExemploRequest("", "");

        mockMvc.perform(post("/api/v1/exemplos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"))
                .andExpect(jsonPath("$.details").isArray());

        verifyNoInteractions(exemploService);
    }

    @Test
    void createReturnsBadRequestWhenServiceThrowsBusinessException() throws Exception {
        when(exemploService.create(request)).thenThrow(new BusinessException("Regra de negócio inválida"));

        mockMvc.perform(post("/api/v1/exemplos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Regra de negócio inválida"))
                .andExpect(jsonPath("$.path").value("/api/v1/exemplos"));

        verify(exemploService).create(request);
        verifyNoMoreInteractions(exemploService);
    }

    @Test
    void findByIdReturnsOkResponse() throws Exception {
        when(exemploService.findById(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/exemplos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));

        verify(exemploService).findById(id);
        verifyNoMoreInteractions(exemploService);
    }

    @Test
    void findByIdReturnsNotFoundWhenServiceThrows() throws Exception {
        when(exemploService.findById(id)).thenThrow(new ResourceNotFoundException("Exemplo não encontrado"));

        mockMvc.perform(get("/api/v1/exemplos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Exemplo não encontrado"));

        verify(exemploService).findById(id);
        verifyNoMoreInteractions(exemploService);
    }

    @Test
    void findAllReturnsOkResponse() throws Exception {
        when(exemploService.findAll()).thenReturn(Set.of(response));

        mockMvc.perform(get("/api/v1/exemplos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id.toString()));

        verify(exemploService).findAll();
        verifyNoMoreInteractions(exemploService);
    }

    @Test
    void findAllReturnsEmptyListWhenThereAreNoActiveEntities() throws Exception {
        when(exemploService.findAll()).thenReturn(Set.of());

        mockMvc.perform(get("/api/v1/exemplos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(exemploService).findAll();
        verifyNoMoreInteractions(exemploService);
    }

    @Test
    void findAllReturnsInternalServerErrorWhenServiceThrowsUnexpectedException() throws Exception {
        when(exemploService.findAll()).thenThrow(new IllegalStateException("Falha inesperada"));

        mockMvc.perform(get("/api/v1/exemplos"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro interno inesperado"))
                .andExpect(jsonPath("$.path").value("/api/v1/exemplos"));

        verify(exemploService).findAll();
        verifyNoMoreInteractions(exemploService);
    }

    @Test
    void updateReturnsOkResponse() throws Exception {
        when(exemploService.update(id, request)).thenReturn(response);

        mockMvc.perform(put("/api/v1/exemplos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value(request.nome()));

        verify(exemploService).update(id, request);
        verifyNoMoreInteractions(exemploService);
    }

    @Test
    void updateReturnsNotFoundWhenServiceThrows() throws Exception {
        when(exemploService.update(id, request)).thenThrow(new ResourceNotFoundException("Exemplo não encontrado"));

        mockMvc.perform(put("/api/v1/exemplos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Exemplo não encontrado"));

        verify(exemploService).update(id, request);
        verifyNoMoreInteractions(exemploService);
    }

    @Test
    void updateReturnsBadRequestWhenPayloadIsInvalid() throws Exception {
        ExemploRequest invalidRequest = new ExemploRequest("", "");

        mockMvc.perform(put("/api/v1/exemplos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[0]", containsString("obrigat")));

        verifyNoInteractions(exemploService);
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/exemplos/{id}", id))
                .andExpect(status().isNoContent());

        verify(exemploService).delete(id);
        verifyNoMoreInteractions(exemploService);
    }

    @Test
    void deleteReturnsNotFoundWhenServiceThrows() throws Exception {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Exemplo não encontrado"))
                .when(exemploService)
                .delete(id);

        mockMvc.perform(delete("/api/v1/exemplos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Exemplo não encontrado"));

        verify(exemploService).delete(id);
        verifyNoMoreInteractions(exemploService);
    }
}
