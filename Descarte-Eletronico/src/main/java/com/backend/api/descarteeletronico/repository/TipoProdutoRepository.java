package com.backend.api.descarteeletronico.repository;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.tipoproduto.TipoProduto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TipoProdutoRepository extends JpaRepository<TipoProduto, UUID> {

  Optional<TipoProduto> findByIdAndEntityStatus(UUID id, EntityStatus entityStatus);

  Set<TipoProduto> findAllByEntityStatus(EntityStatus entityStatus);

  Set<TipoProduto> findAllByIdInAndEntityStatus(Set<UUID> ids, EntityStatus entityStatus);
}
