package com.backend.api.descarteeletronico.model.relato;

import com.backend.api.descarteeletronico.model.entity.BaseEntity;
import com.backend.api.descarteeletronico.model.enums.TipoRelato;
import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "relato_problema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RelatoProblema extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "ponto_coleta_id", nullable = false)
  private PontoColeta pontoColeta;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_relato", nullable = false, length = 50)
  private TipoRelato tipoRelato;

  @Column(name = "nome", nullable = false, length = 100)
  private String nome;

  @Column(name = "email", nullable = false, length = 100)
  private String email;

  @Column(name = "observacao", nullable = true, length = 1000)
  private String observacao;
}