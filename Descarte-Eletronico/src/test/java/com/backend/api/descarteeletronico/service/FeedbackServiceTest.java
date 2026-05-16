package com.backend.api.descarteeletronico.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.mapper.FeedbackMapper;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.feedback.Feedback;
import com.backend.api.descarteeletronico.model.feedback.dto.FeedbackRequest;
import com.backend.api.descarteeletronico.model.feedback.dto.FeedbackResponse;
import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import com.backend.api.descarteeletronico.repository.FeedbackRepository;
import com.backend.api.descarteeletronico.repository.PontoColetaRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

  @Mock private FeedbackRepository feedbackRepository;

  @Mock private PontoColetaRepository pontoColetaRepository;

  @Mock private FeedbackMapper feedbackMapper;

  @Mock private NotificacaoService notificacaoService;

  @InjectMocks private FeedbackService feedbackService;

  private UUID id;
  private UUID pontoColetaId;
  private PontoColeta pontoColeta;
  private Feedback feedback;
  private FeedbackRequest request;
  private FeedbackResponse response;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    pontoColetaId = UUID.randomUUID();
    pontoColeta =
        new PontoColeta(
            "EcoPonto Centro",
            "Rua das Flores, 123",
            "Recebe eletrônicos",
            new BigDecimal("-23.5505200"),
            new BigDecimal("-46.6333080"),
            Set.of());
    request = new FeedbackRequest("Maria Silva", "maria@email.com", "Muito bom.");
    feedback = new Feedback(pontoColeta, request.nome(), request.email(), request.mensagem());
    response =
        new FeedbackResponse(
            id,
            pontoColetaId,
            pontoColeta.getNome(),
            request.nome(),
            request.email(),
            request.mensagem(),
            0L,
            null,
            null,
            EntityStatus.ACTIVE,
            null);
  }

  @Test
  void createSavesActiveFeedbackCreatesNotificationAndReturnsResponse() {
    when(pontoColetaRepository.findByIdAndEntityStatus(pontoColetaId, EntityStatus.ACTIVE))
        .thenReturn(Optional.of(pontoColeta));
    when(feedbackMapper.toEntity(request)).thenReturn(feedback);
    when(feedbackRepository.save(feedback)).thenReturn(feedback);
    when(feedbackMapper.toResponse(feedback)).thenReturn(response);

    FeedbackResponse result = feedbackService.create(pontoColetaId, request);

    assertThat(result).isEqualTo(response);
    assertThat(feedback.getPontoColeta()).isEqualTo(pontoColeta);
    assertThat(feedback.getEntityStatus()).isEqualTo(EntityStatus.ACTIVE);
    assertThat(feedback.getDeletedAt()).isNull();
    verify(pontoColetaRepository).findByIdAndEntityStatus(pontoColetaId, EntityStatus.ACTIVE);
    verify(feedbackMapper).toEntity(request);
    verify(feedbackRepository).save(feedback);
    verify(notificacaoService).createFeedbackRecebido(feedback);
    verify(feedbackMapper).toResponse(feedback);
    verifyNoMoreInteractions(
        pontoColetaRepository, feedbackRepository, feedbackMapper, notificacaoService);
  }

  @Test
  void createThrowsWhenPontoColetaDoesNotExist() {
    when(pontoColetaRepository.findByIdAndEntityStatus(pontoColetaId, EntityStatus.ACTIVE))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> feedbackService.create(pontoColetaId, request))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Ponto de coleta não encontrado");
    verify(pontoColetaRepository).findByIdAndEntityStatus(pontoColetaId, EntityStatus.ACTIVE);
    verifyNoInteractions(feedbackRepository, feedbackMapper, notificacaoService);
    verifyNoMoreInteractions(pontoColetaRepository);
  }

  @Test
  void findAllReturnsNonDeletedFeedbacks() {
    Set<Feedback> feedbacks = Set.of(feedback);
    Set<FeedbackResponse> responses = Set.of(response);
    when(feedbackRepository.findAllByEntityStatusNot(EntityStatus.DELETED)).thenReturn(feedbacks);
    when(feedbackMapper.toResponseSet(feedbacks)).thenReturn(responses);

    Set<FeedbackResponse> result = feedbackService.findAll();

    assertThat(result).isEqualTo(responses);
    verify(feedbackRepository).findAllByEntityStatusNot(EntityStatus.DELETED);
    verify(feedbackMapper).toResponseSet(feedbacks);
    verifyNoMoreInteractions(feedbackRepository, feedbackMapper);
    verifyNoInteractions(pontoColetaRepository, notificacaoService);
  }

  @Test
  void markAsViewedMarksFeedbackAndRelatedNotificationsAsInactive() {
    when(feedbackRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED))
        .thenReturn(Optional.of(feedback));
    when(feedbackRepository.save(feedback)).thenReturn(feedback);
    when(feedbackMapper.toResponse(feedback)).thenReturn(response);

    FeedbackResponse result = feedbackService.markAsViewed(id);

    assertThat(result).isEqualTo(response);
    assertThat(feedback.getEntityStatus()).isEqualTo(EntityStatus.INACTIVE);
    verify(feedbackRepository).findByIdAndEntityStatusNot(id, EntityStatus.DELETED);
    verify(notificacaoService).markFeedbackNotificationsAsViewed(id);
    verify(feedbackRepository).save(feedback);
    verify(feedbackMapper).toResponse(feedback);
    verifyNoMoreInteractions(feedbackRepository, feedbackMapper, notificacaoService);
    verifyNoInteractions(pontoColetaRepository);
  }

  @Test
  void deleteMarksFeedbackAsDeleted() {
    when(feedbackRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED))
        .thenReturn(Optional.of(feedback));

    feedbackService.delete(id);

    assertThat(feedback.getEntityStatus()).isEqualTo(EntityStatus.DELETED);
    assertThat(feedback.getDeletedAt()).isNotNull();
    verify(feedbackRepository).findByIdAndEntityStatusNot(id, EntityStatus.DELETED);
    verify(feedbackRepository).save(feedback);
    verifyNoMoreInteractions(feedbackRepository);
    verifyNoInteractions(pontoColetaRepository, feedbackMapper, notificacaoService);
  }

  @Test
  void deleteThrowsWhenFeedbackDoesNotExist() {
    when(feedbackRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> feedbackService.delete(id))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Feedback não encontrado");
    verify(feedbackRepository).findByIdAndEntityStatusNot(id, EntityStatus.DELETED);
    verify(feedbackRepository, never()).save(feedback);
    verifyNoInteractions(pontoColetaRepository, feedbackMapper, notificacaoService);
    verifyNoMoreInteractions(feedbackRepository);
  }
}
