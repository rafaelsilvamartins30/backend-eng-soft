package com.backend.api.descarteeletronico.model.usuario;

import com.backend.api.descarteeletronico.model.entity.BaseEntity;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.role.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "usuarios_admin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario extends BaseEntity implements UserDetails {

  @Column(name = "nome", nullable = false, length = 100)
  private String nome;

  @Column(name = "email", nullable = false, unique = true, length = 100)
  private String email;

  @Column(name = "senha", nullable = false, length = 255)
  private String senha;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "usuarios_admin_roles",
      joinColumns = @JoinColumn(name = "usuario_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new LinkedHashSet<>();

  @Override
  public Set<GrantedAuthority> getAuthorities() {
    return roles.stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getNome().name()))
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public String getPassword() {
    return senha;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return getEntityStatus() == EntityStatus.ACTIVE;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return getEntityStatus() == EntityStatus.ACTIVE;
  }
}
