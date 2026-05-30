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
import com.backend.api.descarteeletronico.model.enums.TipoRelato;
import com.backend.api.descarteeletronico.model.relato.RelatoProblema;
import com.backend.api.descarteeletronico.model.notificacao.Notificacao;
import com.backend.api.descarteeletronico.model.notificacao.dto.NotificacaoResponse;
import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import com.backend.api.descarteeletronico.repository.NotificacaoRepository;
import java.math.BigDecimal;
import java.time.LocalTime;
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

  @Mock private NotificacaoMapper notificacaoMapper;

  @InjectMocks private NotificacaoService notificacaoService;

  private UUID id;
  private PontoColeta pontoColeta;
  private RelatoProblema relato;
  private Notificacao notificacao;
  private NotificacaoResponse response;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    pontoColeta =
            new PontoColeta(
                    "EcoPonto Centro",
                    "Rua das Flores, 123",
                    "Recebe eletrônicos",
                    new BigDecimal("-23.5505200"),
                    new BigDecimal("-46.6333080"),
                    LocalTime.of(8, 0),
                    LocalTime.of(18, 0),
                    Set.of());

    relato = new RelatoProblema(pontoColeta, TipoRelato.LIXEIRA_CHEIA, "Maria Silva", "maria@email.com", "Muito cheia");
    relato.setId(UUID.randomUUID());

    notificacao =
            new Notificacao(
                    "Lixeira Cheia",
                    "O usuário Maria Silva relatou: 'Lixeira reportada como cheia' no ponto EcoPonto Centro.",
                    pontoColeta,
                    relato);
    notificacao.setEntityStatus(EntityStatus.ACTIVE);

    response =
            new NotificacaoResponse(
                    id,
                    notificacao.getTitulo(),
                    notificacao.getMensagem(),
                    UUID.randomUUID(),
                    pontoColeta.getNome(),
                    relato.getId(),
                    0L,
                    null,
                    null,
                    EntityStatus.ACTIVE,
                    null);
  }

  @Test
  void criarNotificacaoDeRelatoSavesActiveNotification() {
    when(notificacaoRepository.save(org.mockito.ArgumentMatchers.any(Notificacao.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    Notificacao result = notificacaoService.criarNotificacaoDeRelato(relato);

    assertThat(result.getTitulo()).isEqualTo("Lixeira Cheia");
    assertThat(result.getEntityStatus()).isEqualTo(EntityStatus.ACTIVE);
    assertThat(result.getRelatoProblema()).isEqualTo(relato);
    assertThat(result.getPontoColeta()).isEqualTo(pontoColeta);

    verify(notificacaoRepository).save(org.mockito.ArgumentMatchers.any(Notificacao.class));
    verifyNoMoreInteractions(notificacaoRepository);
    verifyNoInteractions(notificacaoMapper);
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
  }

  @Test
  void markNotificacoesDoRelatoComoVistasUpdatesStatus() {
    Set<Notificacao> notificacoes = Set.of(notificacao);
    when(notificacaoRepository.findAllByRelatoProblemaIdAndEntityStatus(relato.getId(), EntityStatus.ACTIVE))
            .thenReturn(notificacoes);

    notificacaoService.markNotificacoesDoRelatoComoVistas(relato.getId());

    assertThat(notificacao.getEntityStatus()).isEqualTo(EntityStatus.INACTIVE);
    verify(notificacaoRepository).findAllByRelatoProblemaIdAndEntityStatus(relato.getId(), EntityStatus.ACTIVE);
    verify(notificacaoRepository).saveAll(notificacoes);
    verifyNoMoreInteractions(notificacaoRepository);
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
    verifyNoInteractions(notificacaoMapper);
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
    verifyNoInteractions(notificacaoMapper);
    verifyNoMoreInteractions(notificacaoRepository);
  }
}