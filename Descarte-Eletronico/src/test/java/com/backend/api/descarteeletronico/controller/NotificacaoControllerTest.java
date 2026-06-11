package com.backend.api.descarteeletronico.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.api.descarteeletronico.exception.GlobalExceptionHandler;
import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.notificacao.dto.NotificacaoResponse;
import com.backend.api.descarteeletronico.service.NotificacaoService;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class NotificacaoControllerTest {

  private MockMvc mockMvc;
  private NotificacaoService notificacaoService;
  private UUID id;
  private NotificacaoResponse response;

  @BeforeEach
  void setUp() {
    notificacaoService = mock(NotificacaoService.class);
    mockMvc =
            MockMvcBuilders.standaloneSetup(new NotificacaoController(notificacaoService))
                    .setControllerAdvice(new GlobalExceptionHandler())
                    .build();

    id = UUID.randomUUID();
    response =
            new NotificacaoResponse(
                    id,
                    "Lixeira Cheia",
                    "O ponto de coleta EcoPonto Centro foi reportado como cheio.",
                    UUID.randomUUID(),
                    "EcoPonto Centro",
                    UUID.randomUUID(),
                    0L,
                    null,
                    null,
                    EntityStatus.ACTIVE,
                    null);
  }

  @Nested
  @Order(2)
  @DisplayName("Cenários de Consulta")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class ConsultaTests {

    @Test
    @Order(1)
    void findAllReturnsOkResponse() throws Exception {
      when(notificacaoService.findAll()).thenReturn(Set.of(response));

      mockMvc
              .perform(get("/api/v1/notificacoes"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$[0].id").value(id.toString()));

      verify(notificacaoService).findAll();
      verifyNoMoreInteractions(notificacaoService);
    }

    @Test
    @Order(2)
    void findUnreadReturnsOkResponse() throws Exception {
      when(notificacaoService.findUnread()).thenReturn(Set.of(response));

      mockMvc
              .perform(get("/api/v1/notificacoes/nao-visualizadas"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$[0].entityStatus").value(EntityStatus.ACTIVE.name()));

      verify(notificacaoService).findUnread();
      verifyNoMoreInteractions(notificacaoService);
    }
  }

  @Nested
  @Order(3)
  @DisplayName("Cenários de Atualização")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class AtualizacaoTests {

    @Test
    @Order(1)
    void markAsViewedReturnsOkResponse() throws Exception {
      NotificacaoResponse viewedResponse =
              new NotificacaoResponse(
                      response.id(),
                      response.titulo(),
                      response.mensagem(),
                      response.pontoColetaId(),
                      response.pontoColetaNome(),
                      response.relatoProblemaId(),
                      response.version(),
                      response.createdAt(),
                      response.updatedAt(),
                      EntityStatus.INACTIVE,
                      response.deletedAt());
      when(notificacaoService.markAsViewed(id)).thenReturn(viewedResponse);

      mockMvc
              .perform(patch("/api/v1/notificacoes/{id}/visualizar", id))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.entityStatus").value(EntityStatus.INACTIVE.name()));

      verify(notificacaoService).markAsViewed(id);
      verifyNoMoreInteractions(notificacaoService);
    }

    @Test
    @Order(2)
    void markAsViewedReturnsNotFoundWhenServiceThrows() throws Exception {
      when(notificacaoService.markAsViewed(id))
              .thenThrow(new ResourceNotFoundException("Notificação não encontrada"));

      mockMvc
              .perform(patch("/api/v1/notificacoes/{id}/visualizar", id))
              .andExpect(status().isNotFound())
              .andExpect(jsonPath("$.message").value("Notificação não encontrada"));

      verify(notificacaoService).markAsViewed(id);
      verifyNoMoreInteractions(notificacaoService);
    }
  }

  @Nested
  @Order(4)
  @DisplayName("Cenários de Exclusão")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class ExclusaoTests {

    @Test
    @Order(1)
    void deleteReturnsNoContent() throws Exception {
      mockMvc.perform(delete("/api/v1/notificacoes/{id}", id)).andExpect(status().isNoContent());

      verify(notificacaoService).delete(id);
      verifyNoMoreInteractions(notificacaoService);
    }

    @Test
    @Order(2)
    void deleteReturnsNotFoundWhenServiceThrows() throws Exception {
      doThrow(new ResourceNotFoundException("Notificação não encontrada"))
              .when(notificacaoService)
              .delete(id);

      mockMvc
              .perform(delete("/api/v1/notificacoes/{id}", id))
              .andExpect(status().isNotFound())
              .andExpect(jsonPath("$.message").value("Notificação não encontrada"));

      verify(notificacaoService).delete(id);
      verifyNoMoreInteractions(notificacaoService);
    }
  }
}