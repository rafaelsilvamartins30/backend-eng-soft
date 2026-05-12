package com.backend.api.descarteeletronico.model.pontocoleta;

import com.backend.api.descarteeletronico.model.entity.BaseEntity;
import com.backend.api.descarteeletronico.model.tipoproduto.TipoProduto;
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

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "ponto_coleta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PontoColeta extends BaseEntity {

  @Column(name = "nome", nullable = false, length = 150)
  private String nome;

  @Column(name = "endereco", nullable = false)
  private String endereco;

  @Column(name = "descricao", nullable = false, length = 500)
  private String descricao;

  @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
  private BigDecimal latitude;

  @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
  private BigDecimal longitude;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "ponto_coleta_tipo_produto",
      joinColumns = @JoinColumn(name = "ponto_coleta_id"),
      inverseJoinColumns = @JoinColumn(name = "tipo_produto_id"))
  private Set<TipoProduto> tiposProduto = new LinkedHashSet<>();
}
