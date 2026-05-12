package com.backend.api.descarteeletronico.repository;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PontoColetaRepository extends JpaRepository<PontoColeta, UUID> {

  @EntityGraph(attributePaths = "tiposProduto")
  Optional<PontoColeta> findByIdAndEntityStatus(UUID id, EntityStatus entityStatus);

  @EntityGraph(attributePaths = "tiposProduto")
  Set<PontoColeta> findAllByEntityStatus(EntityStatus entityStatus);
}
