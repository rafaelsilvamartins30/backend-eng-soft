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
import com.backend.api.descarteeletronico.model.enums.NotificacaoTipo;
import com.backend.api.descarteeletronico.model.feedback.dto.FeedbackRequest;
import com.backend.api.descarteeletronico.model.feedback.dto.FeedbackResponse;
import com.backend.api.descarteeletronico.model.notificacao.dto.NotificacaoResponse;
import com.backend.api.descarteeletronico.model.pontocoleta.dto.PontoColetaRequest;
import com.backend.api.descarteeletronico.model.pontocoleta.dto.PontoColetaResponse;
import com.backend.api.descarteeletronico.service.FeedbackService;
import com.backend.api.descarteeletronico.service.NotificacaoService;
import com.backend.api.descarteeletronico.service.PontoColetaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PontoColetaControllerTest {

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private PontoColetaService pontoColetaService;
  private FeedbackService feedbackService;
  private NotificacaoService notificacaoService;
  private UUID id;
  private UUID tipoProdutoId;
  private PontoColetaRequest request;
  private PontoColetaResponse response;

  @BeforeEach
  void setUp() {
    pontoColetaService = mock(PontoColetaService.class);
    feedbackService = mock(FeedbackService.class);
    notificacaoService = mock(NotificacaoService.class);
    mockMvc =
        MockMvcBuilders.standaloneSetup(
                new PontoColetaController(pontoColetaService, feedbackService, notificacaoService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    objectMapper = new ObjectMapper();

    id = UUID.randomUUID();
    tipoProdutoId = UUID.randomUUID();
    request =
        new PontoColetaRequest(
            "EcoPonto Centro",
            "Rua das Flores, 123",
            "Recebe eletrônicos de pequeno porte",
            new BigDecimal("-23.5505200"),
            new BigDecimal("-46.6333080"),
            Set.of(tipoProdutoId));
    response =
        new PontoColetaResponse(
            id,
            request.nome(),
            request.endereco(),
            request.descricao(),
            request.latitude(),
            request.longitude(),
            Set.of(),
            0L,
            null,
            null,
            EntityStatus.ACTIVE,
            null);
  }

  @Test
  void createReturnsCreatedResponse() throws Exception {
    when(pontoColetaService.create(request)).thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/pontos-coleta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.nome").value(request.nome()));

    verify(pontoColetaService).create(request);
    verifyNoMoreInteractions(pontoColetaService);
    verifyNoInteractions(feedbackService, notificacaoService);
  }

  @Test
  void createReturnsBadRequestWhenPayloadIsInvalid() throws Exception {
    PontoColetaRequest invalidRequest =
        new PontoColetaRequest("", "", "", new BigDecimal("-91"), new BigDecimal("-181"), Set.of());

    mockMvc
        .perform(
            post("/api/v1/pontos-coleta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"));

    verifyNoInteractions(pontoColetaService);
    verifyNoInteractions(feedbackService, notificacaoService);
  }

  @Test
  void createReturnsBadRequestWhenPayloadIsTooLarge() throws Exception {
    PontoColetaRequest invalidRequest =
        new PontoColetaRequest(
            "A".repeat(151),
            "B".repeat(256),
            "C".repeat(501),
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            Set.of(tipoProdutoId));

    mockMvc
        .perform(
            post("/api/v1/pontos-coleta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.details").isArray());

    verifyNoInteractions(pontoColetaService);
    verifyNoInteractions(feedbackService, notificacaoService);
  }

  @Test
  void createReturnsBadRequestWhenServiceThrowsBusinessException() throws Exception {
    when(pontoColetaService.create(request)).thenThrow(new BusinessException("Tipo inválido"));

    mockMvc
        .perform(
            post("/api/v1/pontos-coleta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Tipo inválido"))
        .andExpect(jsonPath("$.path").value("/api/v1/pontos-coleta"));

    verify(pontoColetaService).create(request);
    verifyNoMoreInteractions(pontoColetaService);
    verifyNoInteractions(feedbackService, notificacaoService);
  }

  @Test
  void findByIdReturnsOkResponse() throws Exception {
    when(pontoColetaService.findById(id)).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/pontos-coleta/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()));

    verify(pontoColetaService).findById(id);
    verifyNoMoreInteractions(pontoColetaService);
    verifyNoInteractions(feedbackService, notificacaoService);
  }

  @Test
  void findByIdReturnsNotFoundWhenServiceThrows() throws Exception {
    when(pontoColetaService.findById(id))
        .thenThrow(new ResourceNotFoundException("Ponto de coleta não encontrado"));

    mockMvc
        .perform(get("/api/v1/pontos-coleta/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Ponto de coleta não encontrado"));

    verify(pontoColetaService).findById(id);
    verifyNoMoreInteractions(pontoColetaService);
    verifyNoInteractions(feedbackService, notificacaoService);
  }

  @Test
  void findAllReturnsOkResponse() throws Exception {
    when(pontoColetaService.findAll()).thenReturn(Set.of(response));

    mockMvc
        .perform(get("/api/v1/pontos-coleta"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(id.toString()));

    verify(pontoColetaService).findAll();
    verifyNoMoreInteractions(pontoColetaService);
    verifyNoInteractions(feedbackService, notificacaoService);
  }

  @Test
  void findAllReturnsEmptyListWhenThereAreNoActiveEntities() throws Exception {
    when(pontoColetaService.findAll()).thenReturn(Set.of());

    mockMvc
        .perform(get("/api/v1/pontos-coleta"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());

    verify(pontoColetaService).findAll();
    verifyNoMoreInteractions(pontoColetaService);
    verifyNoInteractions(feedbackService, notificacaoService);
  }

  @Test
  void findAllReturnsInternalServerErrorWhenServiceThrowsUnexpectedException() throws Exception {
    when(pontoColetaService.findAll()).thenThrow(new IllegalStateException("Falha inesperada"));

    mockMvc
        .perform(get("/api/v1/pontos-coleta"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value("Erro interno inesperado"))
        .andExpect(jsonPath("$.path").value("/api/v1/pontos-coleta"));

    verify(pontoColetaService).findAll();
    verifyNoMoreInteractions(pontoColetaService);
    verifyNoInteractions(feedbackService, notificacaoService);
  }

  @Test
  void updateReturnsOkResponse() throws Exception {
    when(pontoColetaService.update(id, request)).thenReturn(response);

    mockMvc
        .perform(
            put("/api/v1/pontos-coleta/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()));

    verify(pontoColetaService).update(id, request);
    verifyNoMoreInteractions(pontoColetaService);
    verifyNoInteractions(feedbackService, notificacaoService);
  }

  @Test
  void updateReturnsBadRequestWhenPayloadIsInvalid() throws Exception {
    PontoColetaRequest invalidRequest =
        new PontoColetaRequest("", "", "", new BigDecimal("91"), new BigDecimal("181"), Set.of());

    mockMvc
        .perform(
            put("/api/v1/pontos-coleta/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"));

    verifyNoInteractions(pontoColetaService);
    verifyNoInteractions(feedbackService, notificacaoService);
  }

  @Test
  void updateReturnsNotFoundWhenServiceThrows() throws Exception {
    when(pontoColetaService.update(id, request))
        .thenThrow(new ResourceNotFoundException("Ponto de coleta não encontrado"));

    mockMvc
        .perform(
            put("/api/v1/pontos-coleta/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Ponto de coleta não encontrado"));

    verify(pontoColetaService).update(id, request);
    verifyNoMoreInteractions(pontoColetaService);
    verifyNoInteractions(feedbackService, notificacaoService);
  }

  @Test
  void updateReturnsBadRequestWhenServiceThrowsBusinessException() throws Exception {
    when(pontoColetaService.update(id, request)).thenThrow(new BusinessException("Tipo inválido"));

    mockMvc
        .perform(
            put("/api/v1/pontos-coleta/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Tipo inválido"));

    verify(pontoColetaService).update(id, request);
    verifyNoMoreInteractions(pontoColetaService);
    verifyNoInteractions(feedbackService, notificacaoService);
  }

  @Test
  void deleteReturnsNoContent() throws Exception {
    mockMvc.perform(delete("/api/v1/pontos-coleta/{id}", id)).andExpect(status().isNoContent());

    verify(pontoColetaService).delete(id);
    verifyNoMoreInteractions(pontoColetaService);
    verifyNoInteractions(feedbackService, notificacaoService);
  }

  @Test
  void deleteReturnsNotFoundWhenServiceThrows() throws Exception {
    doThrow(new ResourceNotFoundException("Ponto de coleta não encontrado"))
        .when(pontoColetaService)
        .delete(id);

    mockMvc
        .perform(delete("/api/v1/pontos-coleta/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Ponto de coleta não encontrado"));

    verify(pontoColetaService).delete(id);
    verifyNoMoreInteractions(pontoColetaService);
    verifyNoInteractions(feedbackService, notificacaoService);
  }

  @Test
  void createFeedbackReturnsCreatedResponse() throws Exception {
    FeedbackRequest feedbackRequest =
        new FeedbackRequest("João Silva", "joao@email.com", "Ponto muito bem localizado.");
    FeedbackResponse feedbackResponse =
        new FeedbackResponse(
            UUID.randomUUID(),
            id,
            response.nome(),
            feedbackRequest.nome(),
            feedbackRequest.email(),
            feedbackRequest.mensagem(),
            0L,
            null,
            null,
            EntityStatus.ACTIVE,
            null);
    when(feedbackService.create(id, feedbackRequest)).thenReturn(feedbackResponse);

    mockMvc
        .perform(
            post("/api/v1/pontos-coleta/{id}/feedbacks", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedbackRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.pontoColetaId").value(id.toString()))
        .andExpect(jsonPath("$.nome").value(feedbackRequest.nome()))
        .andExpect(jsonPath("$.email").value(feedbackRequest.email()));

    verify(feedbackService).create(id, feedbackRequest);
    verifyNoMoreInteractions(feedbackService);
    verifyNoInteractions(pontoColetaService, notificacaoService);
  }

  @Test
  void createFeedbackReturnsBadRequestWhenPayloadIsInvalid() throws Exception {
    FeedbackRequest feedbackRequest = new FeedbackRequest("", "email-invalido", "");

    mockMvc
        .perform(
            post("/api/v1/pontos-coleta/{id}/feedbacks", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedbackRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"));

    verifyNoInteractions(pontoColetaService, feedbackService, notificacaoService);
  }

  @Test
  void notifyFullReturnsCreatedResponse() throws Exception {
    NotificacaoResponse notificacaoResponse =
        new NotificacaoResponse(
            UUID.randomUUID(),
            NotificacaoTipo.PONTO_COLETA_CHEIO,
            "Ponto de coleta cheio",
            "O ponto de coleta EcoPonto Centro foi reportado como cheio.",
            id,
            response.nome(),
            null,
            0L,
            null,
            null,
            EntityStatus.ACTIVE,
            null);
    when(notificacaoService.createPontoCheio(id)).thenReturn(notificacaoResponse);

    mockMvc
        .perform(post("/api/v1/pontos-coleta/{id}/notificacoes/cheio", id))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.tipo").value(NotificacaoTipo.PONTO_COLETA_CHEIO.name()))
        .andExpect(jsonPath("$.pontoColetaId").value(id.toString()));

    verify(notificacaoService).createPontoCheio(id);
    verifyNoMoreInteractions(notificacaoService);
    verifyNoInteractions(pontoColetaService, feedbackService);
  }
}
