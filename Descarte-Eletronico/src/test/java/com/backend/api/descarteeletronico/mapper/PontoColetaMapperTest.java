package com.backend.api.descarteeletronico.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import com.backend.api.descarteeletronico.model.pontocoleta.dto.PontoColetaRequest;
import com.backend.api.descarteeletronico.model.pontocoleta.dto.PontoColetaResponse;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class PontoColetaMapperTest {

  private final PontoColetaMapper mapper = Mappers.getMapper(PontoColetaMapper.class);
  private final TipoProdutoMapper tipoProdutoMapper = Mappers.getMapper(TipoProdutoMapper.class);

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(mapper, "tipoProdutoMapper", tipoProdutoMapper);
  }

  @Nested
  @Order(1)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class MapeamentoParaEntidade {

    @Test
    @Order(1)
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
    @Order(2)
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

  @Nested
  @Order(2)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class MapeamentoParaDTO {

    @Test
    @Order(1)
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
    @Order(2)
    void toResponseSetMapsSet() {
      PontoColeta entity = new PontoColeta();
      entity.setNome("Ponto");
      entity.setHorarioAbertura(LocalTime.of(8, 0));
      entity.setHorarioFechamento(LocalTime.of(18, 0));

      Set<PontoColetaResponse> responses = mapper.toResponseSet(Set.of(entity));

      assertThat(responses).hasSize(1);
      assertThat(responses.iterator().next().nome()).isEqualTo("Ponto");
    }

    @Test
    @Order(3)
    void formatarHorarioReturnsIndisponivelWhenNull() {
      String result = mapper.formatarHorario(null, null);
      assertThat(result).isEqualTo("Horário indisponível");

      result = mapper.formatarHorario(LocalTime.of(8,0), null);
      assertThat(result).isEqualTo("Horário indisponível");
    }

    @Test
    @Order(4)
    void calcularAbertoReturnsFalseWhenNull() {
      boolean result = mapper.calcularAberto(null, null);
      assertThat(result).isFalse();
    }

    @Test
    @Order(5)
    void calcularAbertoReturnsCorrectStatus() {
      LocalTime abertura = LocalTime.of(0, 0);
      LocalTime fechamento = LocalTime.of(23, 59);
      boolean result = mapper.calcularAberto(abertura, fechamento);
      assertThat(result).isTrue();

      abertura = LocalTime.of(23, 58);
      fechamento = LocalTime.of(23, 59);
    }
  }
}
