package com.backend.api.descarteeletronico.model.exemplo;

import com.backend.api.descarteeletronico.model.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exemplo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exemplo extends BaseEntity {

  @Column(name = "nome", nullable = false)
  private String nome;

  @Column(name = "descricao", nullable = false)
  private String descricao;
}
