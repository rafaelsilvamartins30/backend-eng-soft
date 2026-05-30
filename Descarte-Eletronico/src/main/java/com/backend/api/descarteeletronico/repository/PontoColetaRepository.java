package com.backend.api.descarteeletronico.repository;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PontoColetaRepository extends JpaRepository<PontoColeta, UUID> {

  @EntityGraph(attributePaths = "tiposProduto")
  Optional<PontoColeta> findByIdAndEntityStatus(UUID id, EntityStatus entityStatus);

  @EntityGraph(attributePaths = "tiposProduto")
  Set<PontoColeta> findAllByEntityStatus(EntityStatus entityStatus);

  @EntityGraph(attributePaths = "tiposProduto")
  Page<PontoColeta> findAllByEntityStatus(EntityStatus entityStatus, Pageable pageable);
}
