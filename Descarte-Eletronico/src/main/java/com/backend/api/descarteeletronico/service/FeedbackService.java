package com.backend.api.descarteeletronico.service;

import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.mapper.FeedbackMapper;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.feedback.Feedback;
import com.backend.api.descarteeletronico.model.feedback.dto.FeedbackRequest;
import com.backend.api.descarteeletronico.model.feedback.dto.FeedbackResponse;
import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import com.backend.api.descarteeletronico.repository.FeedbackRepository;
import com.backend.api.descarteeletronico.repository.PontoColetaRepository;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackService {

  private final FeedbackRepository feedbackRepository;
  private final PontoColetaRepository pontoColetaRepository;
  private final FeedbackMapper feedbackMapper;
  private final NotificacaoService notificacaoService;

  @Transactional
  public FeedbackResponse create(UUID pontoColetaId, FeedbackRequest request) {
    PontoColeta pontoColeta = findActivePontoColetaById(pontoColetaId);
    Feedback feedback = feedbackMapper.toEntity(request);
    feedback.setPontoColeta(pontoColeta);
    feedback.setEntityStatus(EntityStatus.ACTIVE);
    feedback.setDeletedAt(null);

    Feedback savedFeedback = feedbackRepository.save(feedback);
    notificacaoService.createFeedbackRecebido(savedFeedback);

    return feedbackMapper.toResponse(savedFeedback);
  }

  @Transactional(readOnly = true)
  public Set<FeedbackResponse> findAll() {
    return feedbackMapper.toResponseSet(
        feedbackRepository.findAllByEntityStatusNot(EntityStatus.DELETED));
  }

  @Transactional(readOnly = true)
  public Set<FeedbackResponse> findUnread() {
    return feedbackMapper.toResponseSet(
        feedbackRepository.findAllByEntityStatus(EntityStatus.ACTIVE));
  }

  @Transactional(readOnly = true)
  public FeedbackResponse findById(UUID id) {
    return feedbackMapper.toResponse(findNotDeletedById(id));
  }

  @Transactional
  public FeedbackResponse markAsViewed(UUID id) {
    Feedback feedback = findNotDeletedById(id);
    feedback.setEntityStatus(EntityStatus.INACTIVE);
    notificacaoService.markFeedbackNotificationsAsViewed(id);

    return feedbackMapper.toResponse(feedbackRepository.save(feedback));
  }

  @Transactional
  public void delete(UUID id) {
    Feedback feedback = findNotDeletedById(id);
    feedback.setEntityStatus(EntityStatus.DELETED);
    feedback.setDeletedAt(LocalDateTime.now());

    feedbackRepository.save(feedback);
  }

  private Feedback findNotDeletedById(UUID id) {
    return feedbackRepository
        .findByIdAndEntityStatusNot(id, EntityStatus.DELETED)
        .orElseThrow(() -> new ResourceNotFoundException("Feedback não encontrado"));
  }

  private PontoColeta findActivePontoColetaById(UUID id) {
    return pontoColetaRepository
        .findByIdAndEntityStatus(id, EntityStatus.ACTIVE)
        .orElseThrow(() -> new ResourceNotFoundException("Ponto de coleta não encontrado"));
  }
}
