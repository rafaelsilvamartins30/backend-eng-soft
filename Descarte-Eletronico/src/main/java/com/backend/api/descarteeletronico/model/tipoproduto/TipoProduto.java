package com.backend.api.descarteeletronico.model.tipoproduto;

import com.backend.api.descarteeletronico.model.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tipo_produto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoProduto extends BaseEntity {

  @Column(name = "nome", nullable = false, length = 100)
  private String nome;

  @Column(name = "descricao_exemplos", nullable = false, length = 500)
  private String descricaoExemplos;
}
