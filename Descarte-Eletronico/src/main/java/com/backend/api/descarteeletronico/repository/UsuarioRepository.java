package com.backend.api.descarteeletronico.repository;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByIdAndEntityStatusNot(UUID id, EntityStatus entityStatus);

    Set<Usuario> findAllByEntityStatusNot(EntityStatus entityStatus);

    boolean existsByEmailAndEntityStatusNot(String email, EntityStatus entityStatus);
}