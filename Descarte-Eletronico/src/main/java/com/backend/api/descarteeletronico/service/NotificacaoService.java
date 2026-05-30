package com.backend.api.descarteeletronico.service;

import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.mapper.NotificacaoMapper;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.relato.RelatoProblema;
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
  private final NotificacaoMapper notificacaoMapper;

  @Transactional
  public Notificacao criarNotificacaoDeRelato(RelatoProblema relato) {
    String nomePonto = relato.getPontoColeta().getNome();

    String titulo = gerarTituloNotificacao(relato);

    String mensagem = String.format("O usuário %s relatou: '%s' no ponto %s.",
            relato.getNome(),
            relato.getTipoRelato().getDescricao(),
            nomePonto);

    Notificacao notificacao = new Notificacao(
            titulo,
            mensagem,
            relato.getPontoColeta(),
            relato
    );

    notificacao.setEntityStatus(EntityStatus.ACTIVE);
    notificacao.setDeletedAt(null);

    return notificacaoRepository.saveAndFlush(notificacao);
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
  public void markNotificacoesDoRelatoComoVistas(UUID relatoProblemaId) {
    Set<Notificacao> notificacoes =
            notificacaoRepository.findAllByRelatoProblemaIdAndEntityStatus(relatoProblemaId, EntityStatus.ACTIVE);
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

  private String gerarTituloNotificacao(RelatoProblema relato) {
    return switch (relato.getTipoRelato()) {
      case LIXEIRA_CHEIA -> "Lixeira Cheia";
      case PONTO_NAO_EXISTE -> "Ponto Inexistente";
      case LIXEIRA_DANIFICADA -> "Lixeira Danificada";
      case HORARIO_INCORRETO -> "Horário Incorreto";
      case MATERIAIS_RECUSADOS -> "Materiais Recusados";
      case OUTRO -> "Problema Relatado";
    };
  }
}
