package com.backend.api.descarteeletronico.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.mapper.NotificacaoMapper;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.enums.NotificacaoTipo;
import com.backend.api.descarteeletronico.model.feedback.Feedback;
import com.backend.api.descarteeletronico.model.notificacao.Notificacao;
import com.backend.api.descarteeletronico.model.notificacao.dto.NotificacaoResponse;
import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import com.backend.api.descarteeletronico.repository.NotificacaoRepository;
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
class NotificacaoServiceTest {

  @Mock private NotificacaoRepository notificacaoRepository;

  @Mock private PontoColetaRepository pontoColetaRepository;

  @Mock private NotificacaoMapper notificacaoMapper;

  @InjectMocks private NotificacaoService notificacaoService;

  private UUID id;
  private UUID pontoColetaId;
  private PontoColeta pontoColeta;
  private Feedback feedback;
  private Notificacao notificacao;
  private NotificacaoResponse response;

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
    feedback = new Feedback(pontoColeta, "Maria Silva", "maria@email.com", "Muito bom.");
    notificacao =
        new Notificacao(
            NotificacaoTipo.PONTO_COLETA_CHEIO,
            "Ponto de coleta cheio",
            "O ponto de coleta EcoPonto Centro foi reportado como cheio.",
            pontoColeta,
            null);
    response =
        new NotificacaoResponse(
            id,
            notificacao.getTipo(),
            notificacao.getTitulo(),
            notificacao.getMensagem(),
            pontoColetaId,
            pontoColeta.getNome(),
            null,
            0L,
            null,
            null,
            EntityStatus.ACTIVE,
            null);
  }

  @Test
  void createPontoCheioSavesActiveNotificationAndReturnsResponse() {
    when(pontoColetaRepository.findByIdAndEntityStatus(pontoColetaId, EntityStatus.ACTIVE))
        .thenReturn(Optional.of(pontoColeta));
    when(notificacaoRepository.save(org.mockito.ArgumentMatchers.any(Notificacao.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(notificacaoMapper.toResponse(org.mockito.ArgumentMatchers.any(Notificacao.class)))
        .thenReturn(response);

    NotificacaoResponse result = notificacaoService.createPontoCheio(pontoColetaId);

    assertThat(result).isEqualTo(response);
    verify(pontoColetaRepository).findByIdAndEntityStatus(pontoColetaId, EntityStatus.ACTIVE);
    verify(notificacaoRepository).save(org.mockito.ArgumentMatchers.any(Notificacao.class));
    verify(notificacaoMapper).toResponse(org.mockito.ArgumentMatchers.any(Notificacao.class));
    verifyNoMoreInteractions(pontoColetaRepository, notificacaoRepository, notificacaoMapper);
  }

  @Test
  void createPontoCheioThrowsWhenPontoColetaDoesNotExist() {
    when(pontoColetaRepository.findByIdAndEntityStatus(pontoColetaId, EntityStatus.ACTIVE))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> notificacaoService.createPontoCheio(pontoColetaId))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Ponto de coleta não encontrado");
    verify(pontoColetaRepository).findByIdAndEntityStatus(pontoColetaId, EntityStatus.ACTIVE);
    verifyNoInteractions(notificacaoRepository, notificacaoMapper);
    verifyNoMoreInteractions(pontoColetaRepository);
  }

  @Test
  void createFeedbackRecebidoSavesActiveNotification() {
    when(notificacaoRepository.save(org.mockito.ArgumentMatchers.any(Notificacao.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Notificacao result = notificacaoService.createFeedbackRecebido(feedback);

    assertThat(result.getTipo()).isEqualTo(NotificacaoTipo.FEEDBACK_RECEBIDO);
    assertThat(result.getEntityStatus()).isEqualTo(EntityStatus.ACTIVE);
    assertThat(result.getFeedback()).isEqualTo(feedback);
    assertThat(result.getPontoColeta()).isEqualTo(pontoColeta);
    verify(notificacaoRepository).save(org.mockito.ArgumentMatchers.any(Notificacao.class));
    verifyNoMoreInteractions(notificacaoRepository);
    verifyNoInteractions(pontoColetaRepository, notificacaoMapper);
  }

  @Test
  void findUnreadReturnsActiveNotifications() {
    Set<Notificacao> notificacoes = Set.of(notificacao);
    Set<NotificacaoResponse> responses = Set.of(response);
    when(notificacaoRepository.findAllByEntityStatus(EntityStatus.ACTIVE)).thenReturn(notificacoes);
    when(notificacaoMapper.toResponseSet(notificacoes)).thenReturn(responses);

    Set<NotificacaoResponse> result = notificacaoService.findUnread();

    assertThat(result).isEqualTo(responses);
    verify(notificacaoRepository).findAllByEntityStatus(EntityStatus.ACTIVE);
    verify(notificacaoMapper).toResponseSet(notificacoes);
    verifyNoMoreInteractions(notificacaoRepository, notificacaoMapper);
    verifyNoInteractions(pontoColetaRepository);
  }

  @Test
  void markAsViewedMarksNotificationAsInactive() {
    when(notificacaoRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED))
        .thenReturn(Optional.of(notificacao));
    when(notificacaoRepository.save(notificacao)).thenReturn(notificacao);
    when(notificacaoMapper.toResponse(notificacao)).thenReturn(response);

    NotificacaoResponse result = notificacaoService.markAsViewed(id);

    assertThat(result).isEqualTo(response);
    assertThat(notificacao.getEntityStatus()).isEqualTo(EntityStatus.INACTIVE);
    verify(notificacaoRepository).findByIdAndEntityStatusNot(id, EntityStatus.DELETED);
    verify(notificacaoRepository).save(notificacao);
    verify(notificacaoMapper).toResponse(notificacao);
    verifyNoMoreInteractions(notificacaoRepository, notificacaoMapper);
    verifyNoInteractions(pontoColetaRepository);
  }

  @Test
  void deleteMarksNotificationAsDeleted() {
    when(notificacaoRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED))
        .thenReturn(Optional.of(notificacao));

    notificacaoService.delete(id);

    assertThat(notificacao.getEntityStatus()).isEqualTo(EntityStatus.DELETED);
    assertThat(notificacao.getDeletedAt()).isNotNull();
    verify(notificacaoRepository).findByIdAndEntityStatusNot(id, EntityStatus.DELETED);
    verify(notificacaoRepository).save(notificacao);
    verifyNoMoreInteractions(notificacaoRepository);
    verifyNoInteractions(pontoColetaRepository, notificacaoMapper);
  }

  @Test
  void deleteThrowsWhenNotificationDoesNotExist() {
    when(notificacaoRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> notificacaoService.delete(id))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Notificação não encontrada");
    verify(notificacaoRepository).findByIdAndEntityStatusNot(id, EntityStatus.DELETED);
    verify(notificacaoRepository, never()).save(notificacao);
    verifyNoInteractions(pontoColetaRepository, notificacaoMapper);
    verifyNoMoreInteractions(notificacaoRepository);
  }
}
