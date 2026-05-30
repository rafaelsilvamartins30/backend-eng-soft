package com.backend.api.descarteeletronico.repository;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.relato.RelatoProblema;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelatoProblemaRepository extends JpaRepository<RelatoProblema, UUID> {

    @EntityGraph(attributePaths = "pontoColeta")
    Optional<RelatoProblema> findByIdAndEntityStatusNot(UUID id, EntityStatus entityStatus);

    @EntityGraph(attributePaths = "pontoColeta")
    Set<RelatoProblema> findAllByEntityStatusNot(EntityStatus entityStatus);

    @EntityGraph(attributePaths = "pontoColeta")
    Set<RelatoProblema> findAllByEntityStatus(EntityStatus entityStatus);
}