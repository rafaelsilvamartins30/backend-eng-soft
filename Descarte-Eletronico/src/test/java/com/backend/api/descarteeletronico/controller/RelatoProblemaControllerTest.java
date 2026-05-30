package com.backend.api.descarteeletronico.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.api.descarteeletronico.exception.GlobalExceptionHandler;
import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.enums.TipoRelato;
import com.backend.api.descarteeletronico.model.relato.dto.RelatoProblemaResponse;
import com.backend.api.descarteeletronico.service.RelatoProblemaService;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class RelatoProblemaControllerTest {

  private MockMvc mockMvc;
  private RelatoProblemaService relatoProblemaService;
  private UUID id;
  private RelatoProblemaResponse response;

  @BeforeEach
  void setUp() {
    relatoProblemaService = mock(RelatoProblemaService.class);
    mockMvc =
            MockMvcBuilders.standaloneSetup(new RelatoProblemaController(relatoProblemaService))
                    .setControllerAdvice(new GlobalExceptionHandler())
                    .build();

    id = UUID.randomUUID();
    response =
            new RelatoProblemaResponse(
                    id,
                    UUID.randomUUID(),
                    "EcoPonto Centro",
                    TipoRelato.LIXEIRA_CHEIA,
                    "Maria Silva",
                    "maria@email.com",
                    "Lixeira transbordando.",
                    0L,
                    null,
                    null,
                    EntityStatus.ACTIVE,
                    null);
  }

  @Test
  void findAllReturnsOkResponse() throws Exception {
    when(relatoProblemaService.findAll()).thenReturn(Set.of(response));

    mockMvc
            .perform(get("/api/v1/relatos-problema"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(id.toString()));

    verify(relatoProblemaService).findAll();
    verifyNoMoreInteractions(relatoProblemaService);
  }

  @Test
  void findByIdReturnsOkResponse() throws Exception {
    when(relatoProblemaService.findById(id)).thenReturn(response);

    mockMvc
            .perform(get("/api/v1/relatos-problema/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()));

    verify(relatoProblemaService).findById(id);
    verifyNoMoreInteractions(relatoProblemaService);
  }

  @Test
  void deleteReturnsNoContent() throws Exception {
    mockMvc.perform(delete("/api/v1/relatos-problema/{id}", id)).andExpect(status().isNoContent());

    verify(relatoProblemaService).delete(id);
    verifyNoMoreInteractions(relatoProblemaService);
  }

  @Test
  void deleteReturnsNotFoundWhenServiceThrows() throws Exception {
    doThrow(new ResourceNotFoundException("Relato de problema não encontrado"))
            .when(relatoProblemaService)
            .delete(id);

    mockMvc
            .perform(delete("/api/v1/relatos-problema/{id}", id))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Relato de problema não encontrado"));

    verify(relatoProblemaService).delete(id);
    verifyNoMoreInteractions(relatoProblemaService);
  }
}