package com.backend.api.descarteeletronico.model.role;

import com.backend.api.descarteeletronico.model.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {

  @Enumerated(EnumType.STRING)
  @Column(name = "nome", nullable = false, unique = true, length = 50)
  private RoleName nome;
}
