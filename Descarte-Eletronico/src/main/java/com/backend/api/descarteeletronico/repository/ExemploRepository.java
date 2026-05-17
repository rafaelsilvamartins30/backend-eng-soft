package com.backend.api.descarteeletronico.repository;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.exemplo.Exemplo;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExemploRepository extends JpaRepository<Exemplo, UUID> {

  Optional<Exemplo> findByIdAndEntityStatus(UUID id, EntityStatus entityStatus);

  Set<Exemplo> findAllByEntityStatus(EntityStatus entityStatus);
}
