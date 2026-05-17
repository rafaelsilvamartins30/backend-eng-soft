package com.backend.api.descarteeletronico.repository;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.feedback.Feedback;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {

  @EntityGraph(attributePaths = "pontoColeta")
  Optional<Feedback> findByIdAndEntityStatusNot(UUID id, EntityStatus entityStatus);

  @EntityGraph(attributePaths = "pontoColeta")
  Set<Feedback> findAllByEntityStatusNot(EntityStatus entityStatus);

  @EntityGraph(attributePaths = "pontoColeta")
  Set<Feedback> findAllByEntityStatus(EntityStatus entityStatus);
}
