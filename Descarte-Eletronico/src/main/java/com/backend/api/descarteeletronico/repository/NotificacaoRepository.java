package com.backend.api.descarteeletronico.repository;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.notificacao.Notificacao;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacaoRepository extends JpaRepository<Notificacao, UUID> {

  @EntityGraph(attributePaths = {"pontoColeta", "relatoProblema"})
  Optional<Notificacao> findByIdAndEntityStatusNot(UUID id, EntityStatus entityStatus);

  @EntityGraph(attributePaths = {"pontoColeta", "relatoProblema"})
  Set<Notificacao> findAllByEntityStatusNot(EntityStatus entityStatus);

  @EntityGraph(attributePaths = {"pontoColeta", "relatoProblema"})
  Set<Notificacao> findAllByEntityStatus(EntityStatus entityStatus);

  @EntityGraph(attributePaths = {"pontoColeta", "relatoProblema"})
  Set<Notificacao> findAllByRelatoProblemaIdAndEntityStatus(UUID relatoProblemaId, EntityStatus entityStatus);
}