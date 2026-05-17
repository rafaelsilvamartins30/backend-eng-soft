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
import com.backend.api.descarteeletronico.model.feedback.dto.FeedbackResponse;
import com.backend.api.descarteeletronico.service.FeedbackService;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class FeedbackControllerTest {

  private MockMvc mockMvc;
  private FeedbackService feedbackService;
  private UUID id;
  private FeedbackResponse response;

  @BeforeEach
  void setUp() {
    feedbackService = mock(FeedbackService.class);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new FeedbackController(feedbackService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    id = UUID.randomUUID();
    response =
        new FeedbackResponse(
            id,
            UUID.randomUUID(),
            "EcoPonto Centro",
            "Maria Silva",
            "maria@email.com",
            "Muito bom.",
            0L,
            null,
            null,
            EntityStatus.ACTIVE,
            null);
  }

  @Test
  void findAllReturnsOkResponse() throws Exception {
    when(feedbackService.findAll()).thenReturn(Set.of(response));

    mockMvc
        .perform(get("/api/v1/feedbacks"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(id.toString()));

    verify(feedbackService).findAll();
    verifyNoMoreInteractions(feedbackService);
  }

  @Test
  void findUnreadReturnsOkResponse() throws Exception {
    when(feedbackService.findUnread()).thenReturn(Set.of(response));

    mockMvc
        .perform(get("/api/v1/feedbacks/nao-visualizados"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].entityStatus").value(EntityStatus.ACTIVE.name()));

    verify(feedbackService).findUnread();
    verifyNoMoreInteractions(feedbackService);
  }

  @Test
  void findByIdReturnsOkResponse() throws Exception {
    when(feedbackService.findById(id)).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/feedbacks/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()));

    verify(feedbackService).findById(id);
    verifyNoMoreInteractions(feedbackService);
  }

  @Test
  void markAsViewedReturnsOkResponse() throws Exception {
    FeedbackResponse viewedResponse =
        new FeedbackResponse(
            response.id(),
            response.pontoColetaId(),
            response.pontoColetaNome(),
            response.nome(),
            response.email(),
            response.mensagem(),
            response.version(),
            response.createdAt(),
            response.updatedAt(),
            EntityStatus.INACTIVE,
            response.deletedAt());
    when(feedbackService.markAsViewed(id)).thenReturn(viewedResponse);

    mockMvc
        .perform(patch("/api/v1/feedbacks/{id}/visualizar", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.entityStatus").value(EntityStatus.INACTIVE.name()));

    verify(feedbackService).markAsViewed(id);
    verifyNoMoreInteractions(feedbackService);
  }

  @Test
  void deleteReturnsNoContent() throws Exception {
    mockMvc.perform(delete("/api/v1/feedbacks/{id}", id)).andExpect(status().isNoContent());

    verify(feedbackService).delete(id);
    verifyNoMoreInteractions(feedbackService);
  }

  @Test
  void deleteReturnsNotFoundWhenServiceThrows() throws Exception {
    doThrow(new ResourceNotFoundException("Feedback não encontrado"))
        .when(feedbackService)
        .delete(id);

    mockMvc
        .perform(delete("/api/v1/feedbacks/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Feedback não encontrado"));

    verify(feedbackService).delete(id);
    verifyNoMoreInteractions(feedbackService);
  }
}
