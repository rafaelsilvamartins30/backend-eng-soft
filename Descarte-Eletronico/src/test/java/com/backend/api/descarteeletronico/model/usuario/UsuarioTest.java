package com.backend.api.descarteeletronico.model.usuario;

import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.role.Role;
import com.backend.api.descarteeletronico.model.role.RoleName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioTest {

  @Test
  void getAuthoritiesMapsRolesWithSpringSecurityPrefix() {
    Usuario usuario = new Usuario();
    usuario.setRoles(Set.of(new Role(RoleName.ADMIN)));

    assertThat(usuario.getAuthorities()).containsExactly(new SimpleGrantedAuthority("ROLE_ADMIN"));
  }

  @Test
  void userDetailsUsesEmailAsUsernameAndSenhaAsPassword() {
    Usuario usuario = new Usuario();
    usuario.setEmail("admin@descarte.local");
    usuario.setSenha("senha-codificada");
    usuario.setEntityStatus(EntityStatus.ACTIVE);

    assertThat(usuario.getUsername()).isEqualTo("admin@descarte.local");
    assertThat(usuario.getPassword()).isEqualTo("senha-codificada");
    assertThat(usuario.isEnabled()).isTrue();
    assertThat(usuario.isAccountNonLocked()).isTrue();
  }
}
