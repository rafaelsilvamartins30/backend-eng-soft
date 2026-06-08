package com.backend.api.descarteeletronico.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import com.backend.api.descarteeletronico.model.pontocoleta.dto.PontoColetaRequest;
import com.backend.api.descarteeletronico.model.pontocoleta.dto.PontoColetaResponse;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

class PontoColetaMapperTest {

  private final PontoColetaMapper mapper = Mappers.getMapper(PontoColetaMapper.class);
  private final TipoProdutoMapper tipoProdutoMapper = Mappers.getMapper(TipoProdutoMapper.class);

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(mapper, "tipoProdutoMapper", tipoProdutoMapper);
  }

  @Test
  void toEntityMapsRequestToEntityIgnoringTechnicalAndCollections() {
    PontoColetaRequest request =
        new PontoColetaRequest(
            "EcoPonto",
            "Rua A",
            "Desc",
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            LocalTime.of(8, 0),
            LocalTime.of(18, 0),
            Set.of(UUID.randomUUID()));

    PontoColeta entity = mapper.toEntity(request);

    assertThat(entity.getNome()).isEqualTo(request.nome());
    assertThat(entity.getEndereco()).isEqualTo(request.endereco());
    // No PontoColetaMapper.java, o tiposProduto é ignorado explicitamente
    assertThat(entity.getTiposProduto()).isEmpty(); 
  }

  @Test
  void toResponseMapsEntityToResponseWithCalculatedFields() {
    PontoColeta entity =
        new PontoColeta(
            "EcoPonto",
            "Rua A",
            "Desc",
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            LocalTime.of(8, 0),
            LocalTime.of(18, 0),
            Set.of());

    PontoColetaResponse response = mapper.toResponse(entity);

    assertThat(response.nome()).isEqualTo(entity.getNome());
    assertThat(response.horarioFormatado()).isEqualTo("08:00 às 18:00");
  }

  @Test
  void updateEntityFromRequestUpdatesBasicFields() {
    PontoColeta entity = new PontoColeta();
    PontoColetaRequest request =
        new PontoColetaRequest(
            "Novo Nome",
            "Novo Endereço",
            "Nova Desc",
            BigDecimal.ONE,
            BigDecimal.ONE,
            LocalTime.of(9, 0),
            LocalTime.of(17, 0),
            Set.of());

    mapper.updateEntityFromRequest(request, entity);

    assertThat(entity.getNome()).isEqualTo(request.nome());
    assertThat(entity.getEndereco()).isEqualTo(request.endereco());
    assertThat(entity.getHorarioAbertura()).isEqualTo(request.horarioAbertura());
  }
}
