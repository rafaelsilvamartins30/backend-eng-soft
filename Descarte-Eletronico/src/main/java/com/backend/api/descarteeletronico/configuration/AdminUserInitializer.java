package com.backend.api.descarteeletronico.configuration;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.role.Role;
import com.backend.api.descarteeletronico.model.role.RoleName;
import com.backend.api.descarteeletronico.model.usuario.Usuario;
import com.backend.api.descarteeletronico.repository.RoleRepository;
import com.backend.api.descarteeletronico.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements ApplicationRunner {

  private final UsuarioRepository usuarioRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${app.admin.default-email}")
  private String defaultEmail;

  @Value("${app.admin.default-password}")
  private String defaultPassword;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (usuarioRepository.existsByEntityStatus(EntityStatus.ACTIVE)) {
      return;
    }

    Role adminRole =
        roleRepository
            .findByNomeAndEntityStatus(RoleName.ADMIN, EntityStatus.ACTIVE)
            .orElseGet(() -> roleRepository.save(new Role(RoleName.ADMIN)));

    Usuario usuario = new Usuario();
    usuario.setNome("Administrador");
    usuario.setEmail(defaultEmail);
    usuario.setSenha(passwordEncoder.encode(defaultPassword));
    usuario.setEntityStatus(EntityStatus.ACTIVE);
    usuario.setDeletedAt(null);
    usuario.setRoles(Set.of(adminRole));

    usuarioRepository.save(usuario);
  }
}
