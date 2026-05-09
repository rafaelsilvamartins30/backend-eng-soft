package com.backend.api.descarteeletronico.repository;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.tipoproduto.TipoProduto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TipoProdutoRepository extends JpaRepository<TipoProduto, UUID> {

  Optional<TipoProduto> findByIdAndEntityStatusNot(UUID id, EntityStatus entityStatus);

  Set<TipoProduto> findAllByEntityStatusNot(EntityStatus entityStatus);

  Set<TipoProduto> findAllByIdInAndEntityStatusNot(Set<UUID> ids, EntityStatus entityStatus);
}
