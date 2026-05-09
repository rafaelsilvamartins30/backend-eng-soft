package com.backend.api.descarteeletronico.repository;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PontoColetaRepository extends JpaRepository<PontoColeta, UUID> {

  Optional<PontoColeta> findByIdAndEntityStatusNot(UUID id, EntityStatus entityStatus);

  Set<PontoColeta> findAllByEntityStatusNot(EntityStatus entityStatus);
}
