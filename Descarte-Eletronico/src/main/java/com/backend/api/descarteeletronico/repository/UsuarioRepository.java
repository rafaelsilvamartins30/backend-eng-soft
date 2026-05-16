package com.backend.api.descarteeletronico.repository;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.usuario.Usuario;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

  Optional<Usuario> findByIdAndEntityStatus(UUID id, EntityStatus entityStatus);

  Optional<Usuario> findByEmailAndEntityStatus(String email, EntityStatus entityStatus);

  Set<Usuario> findAllByEntityStatus(EntityStatus entityStatus);

  boolean existsByEmailAndEntityStatus(String email, EntityStatus entityStatus);

  boolean existsByEntityStatus(EntityStatus entityStatus);

  Optional<Usuario> findFirstByEntityStatusOrderByCreatedAtAsc(EntityStatus entityStatus);
}
