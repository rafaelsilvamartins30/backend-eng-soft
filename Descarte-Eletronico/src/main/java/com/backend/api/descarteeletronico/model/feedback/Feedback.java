package com.backend.api.descarteeletronico.model.feedback;

import com.backend.api.descarteeletronico.model.entity.BaseEntity;
import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Feedback extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "ponto_coleta_id", nullable = false)
  private PontoColeta pontoColeta;

  @Column(name = "nome", nullable = false, length = 100)
  private String nome;

  @Column(name = "email", nullable = false, length = 100)
  private String email;

  @Column(name = "mensagem", nullable = false, length = 1000)
  private String mensagem;
}
