package com.backend.api.descarteeletronico.model.notificacao;

import com.backend.api.descarteeletronico.model.entity.BaseEntity;
import com.backend.api.descarteeletronico.model.enums.NotificacaoTipo;
import com.backend.api.descarteeletronico.model.feedback.Feedback;
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
@Table(name = "notificacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notificacao extends BaseEntity {

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo", nullable = false, length = 50)
  private NotificacaoTipo tipo;

  @Column(name = "titulo", nullable = false, length = 150)
  private String titulo;

  @Column(name = "mensagem", nullable = false, length = 500)
  private String mensagem;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ponto_coleta_id")
  private PontoColeta pontoColeta;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "feedback_id")
  private Feedback feedback;
}
