package com.backend.api.descarteeletronico.repository;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.role.Role;
import com.backend.api.descarteeletronico.model.role.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

  Optional<Role> findByIdAndEntityStatus(UUID id, EntityStatus entityStatus);

  Optional<Role> findByNomeAndEntityStatus(RoleName nome, EntityStatus entityStatus);

  Set<Role> findAllByEntityStatus(EntityStatus entityStatus);

  boolean existsByNomeAndEntityStatus(RoleName nome, EntityStatus entityStatus);
}
