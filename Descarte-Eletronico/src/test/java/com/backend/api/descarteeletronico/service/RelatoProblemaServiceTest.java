package com.backend.api.descarteeletronico.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.mapper.RelatoProblemaMapper;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.enums.TipoRelato;
import com.backend.api.descarteeletronico.model.relato.RelatoProblema;
import com.backend.api.descarteeletronico.model.relato.dto.RelatoProblemaRequest;
import com.backend.api.descarteeletronico.model.relato.dto.RelatoProblemaResponse;
import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import com.backend.api.descarteeletronico.repository.PontoColetaRepository;
import com.backend.api.descarteeletronico.repository.RelatoProblemaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
class RelatoProblemaServiceTest {

  @Mock private RelatoProblemaRepository relatoProblemaRepository;

  @Mock private PontoColetaRepository pontoColetaRepository;

  @Mock private RelatoProblemaMapper relatoProblemaMapper;

  @Mock private NotificacaoService notificacaoService;

  @InjectMocks private RelatoProblemaService relatoProblemaService;

  private UUID id;
  private UUID pontoColetaId;
  private PontoColeta pontoColeta;
  private RelatoProblema relato;
  private RelatoProblemaRequest request;
  private RelatoProblemaResponse response;

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
                    LocalTime.of(8, 0),
                    LocalTime.of(18, 0),
                    Set.of());

    request = new RelatoProblemaRequest(TipoRelato.LIXEIRA_CHEIA, "Maria Silva", "maria@email.com", "Lixeira lotada");
    relato = new RelatoProblema(pontoColeta, request.tipoRelato(), request.nome(), request.email(), request.observacao());

    response =
            new RelatoProblemaResponse(
                    id,
                    pontoColetaId,
                    pontoColeta.getNome(),
                    request.tipoRelato(),
                    request.nome(),
                    request.email(),
                    request.observacao(),
                    0L,
                    null,
                    null,
                    EntityStatus.ACTIVE,
                    null);
  }

  @Test
  void createSavesActiveRelatoCreatesNotificationAndReturnsResponse() {
    when(pontoColetaRepository.findByIdAndEntityStatus(pontoColetaId, EntityStatus.ACTIVE))
            .thenReturn(Optional.of(pontoColeta));
    when(relatoProblemaMapper.toEntity(request)).thenReturn(relato);
    when(relatoProblemaRepository.save(relato)).thenReturn(relato);
    when(relatoProblemaMapper.toResponse(relato)).thenReturn(response);

    RelatoProblemaResponse result = relatoProblemaService.create(pontoColetaId, request);

    assertThat(result).isEqualTo(response);
    assertThat(relato.getPontoColeta()).isEqualTo(pontoColeta);
    assertThat(relato.getEntityStatus()).isEqualTo(EntityStatus.ACTIVE);
    assertThat(relato.getDeletedAt()).isNull();

    verify(pontoColetaRepository).findByIdAndEntityStatus(pontoColetaId, EntityStatus.ACTIVE);
    verify(relatoProblemaMapper).toEntity(request);
    verify(relatoProblemaRepository).save(relato);
    verify(notificacaoService).criarNotificacaoDeRelato(relato);
    verify(relatoProblemaMapper).toResponse(relato);
    verifyNoMoreInteractions(
            pontoColetaRepository, relatoProblemaRepository, relatoProblemaMapper, notificacaoService);
  }

  @Test
  void createThrowsWhenPontoColetaDoesNotExist() {
    when(pontoColetaRepository.findByIdAndEntityStatus(pontoColetaId, EntityStatus.ACTIVE))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() -> relatoProblemaService.create(pontoColetaId, request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Ponto de coleta não encontrado");

    verify(pontoColetaRepository).findByIdAndEntityStatus(pontoColetaId, EntityStatus.ACTIVE);
    verifyNoInteractions(relatoProblemaRepository, relatoProblemaMapper, notificacaoService);
    verifyNoMoreInteractions(pontoColetaRepository);
  }

  @Test
  void findAllReturnsNonDeletedRelatos() {
    Set<RelatoProblema> relatos = Set.of(relato);
    Set<RelatoProblemaResponse> responses = Set.of(response);
    when(relatoProblemaRepository.findAllByEntityStatusNot(EntityStatus.DELETED)).thenReturn(relatos);
    when(relatoProblemaMapper.toResponseSet(relatos)).thenReturn(responses);

    Set<RelatoProblemaResponse> result = relatoProblemaService.findAll();

    assertThat(result).isEqualTo(responses);
    verify(relatoProblemaRepository).findAllByEntityStatusNot(EntityStatus.DELETED);
    verify(relatoProblemaMapper).toResponseSet(relatos);
    verifyNoMoreInteractions(relatoProblemaRepository, relatoProblemaMapper);
    verifyNoInteractions(pontoColetaRepository, notificacaoService);
  }

  @Test
  void deleteMarksRelatoAsDeleted() {
    when(relatoProblemaRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED))
            .thenReturn(Optional.of(relato));

    relatoProblemaService.delete(id);

    assertThat(relato.getEntityStatus()).isEqualTo(EntityStatus.DELETED);
    assertThat(relato.getDeletedAt()).isNotNull();
    verify(relatoProblemaRepository).findByIdAndEntityStatusNot(id, EntityStatus.DELETED);
    verify(relatoProblemaRepository).save(relato);
    verifyNoMoreInteractions(relatoProblemaRepository);
    verifyNoInteractions(pontoColetaRepository, relatoProblemaMapper, notificacaoService);
  }

  @Test
  void deleteThrowsWhenRelatoDoesNotExist() {
    when(relatoProblemaRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() -> relatoProblemaService.delete(id))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Relato de problema não encontrado");

    verify(relatoProblemaRepository).findByIdAndEntityStatusNot(id, EntityStatus.DELETED);
    verify(relatoProblemaRepository, never()).save(relato);
    verifyNoInteractions(pontoColetaRepository, relatoProblemaMapper, notificacaoService);
    verifyNoMoreInteractions(relatoProblemaRepository);
  }
}