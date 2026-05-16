package com.backend.api.descarteeletronico.service;

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
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

  private final NotificacaoRepository notificacaoRepository;
  private final PontoColetaRepository pontoColetaRepository;
  private final NotificacaoMapper notificacaoMapper;

  @Transactional
  public NotificacaoResponse createPontoCheio(UUID pontoColetaId) {
    PontoColeta pontoColeta = findActivePontoColetaById(pontoColetaId);
    Notificacao notificacao =
        new Notificacao(
            NotificacaoTipo.PONTO_COLETA_CHEIO,
            "Ponto de coleta cheio",
            "O ponto de coleta " + pontoColeta.getNome() + " foi reportado como cheio.",
            pontoColeta,
            null);
    notificacao.setEntityStatus(EntityStatus.ACTIVE);
    notificacao.setDeletedAt(null);

    return notificacaoMapper.toResponse(notificacaoRepository.save(notificacao));
  }

  @Transactional
  public Notificacao createFeedbackRecebido(Feedback feedback) {
    Notificacao notificacao =
        new Notificacao(
            NotificacaoTipo.FEEDBACK_RECEBIDO,
            "Feedback recebido",
            "Um novo feedback foi enviado para o ponto de coleta "
                + feedback.getPontoColeta().getNome()
                + ".",
            feedback.getPontoColeta(),
            feedback);
    notificacao.setEntityStatus(EntityStatus.ACTIVE);
    notificacao.setDeletedAt(null);

    return notificacaoRepository.save(notificacao);
  }

  @Transactional(readOnly = true)
  public Set<NotificacaoResponse> findAll() {
    return notificacaoMapper.toResponseSet(
        notificacaoRepository.findAllByEntityStatusNot(EntityStatus.DELETED));
  }

  @Transactional(readOnly = true)
  public Set<NotificacaoResponse> findUnread() {
    return notificacaoMapper.toResponseSet(
        notificacaoRepository.findAllByEntityStatus(EntityStatus.ACTIVE));
  }

  @Transactional
  public NotificacaoResponse markAsViewed(UUID id) {
    Notificacao notificacao = findNotDeletedById(id);
    notificacao.setEntityStatus(EntityStatus.INACTIVE);

    return notificacaoMapper.toResponse(notificacaoRepository.save(notificacao));
  }

  @Transactional
  public void markFeedbackNotificationsAsViewed(UUID feedbackId) {
    Set<Notificacao> notificacoes =
        notificacaoRepository.findAllByFeedbackIdAndEntityStatus(feedbackId, EntityStatus.ACTIVE);
    notificacoes.forEach(notificacao -> notificacao.setEntityStatus(EntityStatus.INACTIVE));
    notificacaoRepository.saveAll(notificacoes);
  }

  @Transactional
  public void delete(UUID id) {
    Notificacao notificacao = findNotDeletedById(id);
    notificacao.setEntityStatus(EntityStatus.DELETED);
    notificacao.setDeletedAt(LocalDateTime.now());

    notificacaoRepository.save(notificacao);
  }

  private Notificacao findNotDeletedById(UUID id) {
    return notificacaoRepository
        .findByIdAndEntityStatusNot(id, EntityStatus.DELETED)
        .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));
  }

  private PontoColeta findActivePontoColetaById(UUID id) {
    return pontoColetaRepository
        .findByIdAndEntityStatus(id, EntityStatus.ACTIVE)
        .orElseThrow(() -> new ResourceNotFoundException("Ponto de coleta não encontrado"));
  }
}
