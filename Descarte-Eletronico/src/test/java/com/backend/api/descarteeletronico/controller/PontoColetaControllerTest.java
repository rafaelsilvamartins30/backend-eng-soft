package com.backend.api.descarteeletronico.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.api.descarteeletronico.exception.BusinessException;
import com.backend.api.descarteeletronico.exception.GlobalExceptionHandler;
import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.enums.TipoRelato;
import com.backend.api.descarteeletronico.model.relato.dto.RelatoProblemaRequest;
import com.backend.api.descarteeletronico.model.relato.dto.RelatoProblemaResponse;
import com.backend.api.descarteeletronico.model.pontocoleta.dto.PontoColetaRequest;
import com.backend.api.descarteeletronico.model.pontocoleta.dto.PontoColetaResponse;
import com.backend.api.descarteeletronico.service.PontoColetaService;
import com.backend.api.descarteeletronico.service.RelatoProblemaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class PontoColetaControllerTest {

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private PontoColetaService pontoColetaService;
  private RelatoProblemaService relatoProblemaService;
  private UUID id;
  private UUID tipoProdutoId;
  private PontoColetaRequest request;
  private PontoColetaResponse response;

  @BeforeEach
  void setUp() {
    pontoColetaService = mock(PontoColetaService.class);
    relatoProblemaService = mock(RelatoProblemaService.class);

    mockMvc =
            MockMvcBuilders.standaloneSetup(
                            new PontoColetaController(pontoColetaService, relatoProblemaService))
                    .setControllerAdvice(new GlobalExceptionHandler())
                    .build();

    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    id = UUID.randomUUID();
    tipoProdutoId = UUID.randomUUID();
    request =
            new PontoColetaRequest(
                    "EcoPonto Centro",
                    "Rua das Flores, 123",
                    "Recebe eletrônicos de pequeno porte",
                    new BigDecimal("-23.5505200"),
                    new BigDecimal("-46.6333080"),
                    LocalTime.of(8, 0),
                    LocalTime.of(18, 0),
                    Set.of(tipoProdutoId));
    response =
            new PontoColetaResponse(
                    id,
                    request.nome(),
                    request.endereco(),
                    request.descricao(),
                    request.latitude(),
                    request.longitude(),
                    request.horarioAbertura(),
                    request.horarioFechamento(),
                    true,
                    "08:00 às 18:00",
                    Set.of(),
                    0L,
                    null,
                    null,
                    EntityStatus.ACTIVE,
                    null);
  }

  @Nested
  @Order(1)
  @DisplayName("Cenários de Cadastro")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Cadastro {

    @Test
    @Order(1)
    void createReturnsCreatedResponse() throws Exception {
      when(pontoColetaService.create(request)).thenReturn(response);

      mockMvc
              .perform(
                      post("/api/v1/pontos-coleta")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isCreated())
              .andExpect(jsonPath("$.id").value(id.toString()))
              .andExpect(jsonPath("$.nome").value(request.nome()));

      verify(pontoColetaService).create(request);
      verifyNoMoreInteractions(pontoColetaService);
      verifyNoInteractions(relatoProblemaService);
    }

    @Test
    @Order(2)
    void createReturnsBadRequestWhenPayloadIsInvalid() throws Exception {
      PontoColetaRequest invalidRequest =
              new PontoColetaRequest("", "", "", new BigDecimal("-91"), new BigDecimal("-181"), null, null, Set.of());

      mockMvc
              .perform(
                      post("/api/v1/pontos-coleta")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(invalidRequest)))
              .andExpect(status().isBadRequest())
              .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"));

      verifyNoInteractions(pontoColetaService, relatoProblemaService);
    }

    @Test
    @Order(3)
    void createReturnsBadRequestWhenPayloadIsTooLarge() throws Exception {
      PontoColetaRequest invalidRequest =
              new PontoColetaRequest(
                      "A".repeat(151),
                      "B".repeat(256),
                      "C".repeat(501),
                      BigDecimal.ZERO,
                      BigDecimal.ZERO,
                      LocalTime.of(8, 0),
                      LocalTime.of(18, 0),
                      Set.of(tipoProdutoId));

      mockMvc
              .perform(
                      post("/api/v1/pontos-coleta")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(invalidRequest)))
              .andExpect(status().isBadRequest())
              .andExpect(jsonPath("$.details").isArray());

      verifyNoInteractions(pontoColetaService, relatoProblemaService);
    }

    @Test
    @Order(4)
    void createReturnsBadRequestWhenServiceThrowsBusinessException() throws Exception {
      when(pontoColetaService.create(request)).thenThrow(new BusinessException("Tipo inválido"));

      mockMvc
              .perform(
                      post("/api/v1/pontos-coleta")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isBadRequest())
              .andExpect(jsonPath("$.message").value("Tipo inválido"))
              .andExpect(jsonPath("$.path").value("/api/v1/pontos-coleta"));

      verify(pontoColetaService).create(request);
      verifyNoMoreInteractions(pontoColetaService);
      verifyNoInteractions(relatoProblemaService);
    }
  }

  @Nested
  @Order(2)
  @DisplayName("Cenários de Consulta")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Consulta {

    @Test
    @Order(1)
    void findByIdReturnsOkResponse() throws Exception {
      when(pontoColetaService.findById(id)).thenReturn(response);

      mockMvc
              .perform(get("/api/v1/pontos-coleta/{id}", id))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id").value(id.toString()));

      verify(pontoColetaService).findById(id);
      verifyNoMoreInteractions(pontoColetaService);
      verifyNoInteractions(relatoProblemaService);
    }

    @Test
    @Order(2)
    void findByIdReturnsNotFoundWhenServiceThrows() throws Exception {
      when(pontoColetaService.findById(id))
              .thenThrow(new ResourceNotFoundException("Ponto de coleta não encontrado"));

      mockMvc
              .perform(get("/api/v1/pontos-coleta/{id}", id))
              .andExpect(status().isNotFound())
              .andExpect(jsonPath("$.message").value("Ponto de coleta não encontrado"));

      verify(pontoColetaService).findById(id);
      verifyNoMoreInteractions(pontoColetaService);
      verifyNoInteractions(relatoProblemaService);
    }

    @Test
    @Order(3)
    void findAllReturnsOkResponse() throws Exception {
      when(pontoColetaService.findAll()).thenReturn(Set.of(response));

      mockMvc
              .perform(get("/api/v1/pontos-coleta"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$[0].id").value(id.toString()));

      verify(pontoColetaService).findAll();
      verifyNoMoreInteractions(pontoColetaService);
      verifyNoInteractions(relatoProblemaService);
    }

    @Test
    @Order(4)
    void findAllReturnsEmptyListWhenThereAreNoActiveEntities() throws Exception {
      when(pontoColetaService.findAll()).thenReturn(Set.of());

      mockMvc
              .perform(get("/api/v1/pontos-coleta"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$").isArray())
              .andExpect(jsonPath("$").isEmpty());

      verify(pontoColetaService).findAll();
      verifyNoMoreInteractions(pontoColetaService);
      verifyNoInteractions(relatoProblemaService);
    }

    @Test
    @Order(5)
    void findAllReturnsInternalServerErrorWhenServiceThrowsUnexpectedException() throws Exception {
      when(pontoColetaService.findAll()).thenThrow(new IllegalStateException("Falha inesperada"));

      mockMvc
              .perform(get("/api/v1/pontos-coleta"))
              .andExpect(status().isInternalServerError())
              .andExpect(jsonPath("$.message").value("Erro interno inesperado"))
              .andExpect(jsonPath("$.path").value("/api/v1/pontos-coleta"));

      verify(pontoColetaService).findAll();
      verifyNoMoreInteractions(pontoColetaService);
      verifyNoInteractions(relatoProblemaService);
    }
  }

  @Nested
  @Order(3)
  @DisplayName("Cenários de Atualização")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Atualizacao {

    @Test
    @Order(1)
    void updateReturnsOkResponse() throws Exception {
      when(pontoColetaService.update(id, request)).thenReturn(response);

      mockMvc
              .perform(
                      put("/api/v1/pontos-coleta/{id}", id)
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id").value(id.toString()));

      verify(pontoColetaService).update(id, request);
      verifyNoMoreInteractions(pontoColetaService);
      verifyNoInteractions(relatoProblemaService);
    }

    @Test
    @Order(2)
    void updateReturnsBadRequestWhenPayloadIsInvalid() throws Exception {
      PontoColetaRequest invalidRequest =
              new PontoColetaRequest("", "", "", new BigDecimal("91"), new BigDecimal("181"), null, null, Set.of());

      mockMvc
              .perform(
                      put("/api/v1/pontos-coleta/{id}", id)
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(invalidRequest)))
              .andExpect(status().isBadRequest())
              .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"));

      verifyNoInteractions(pontoColetaService, relatoProblemaService);
    }

    @Test
    @Order(3)
    void updateReturnsNotFoundWhenServiceThrows() throws Exception {
      when(pontoColetaService.update(id, request))
              .thenThrow(new ResourceNotFoundException("Ponto de coleta não encontrado"));

      mockMvc
              .perform(
                      put("/api/v1/pontos-coleta/{id}", id)
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isNotFound())
              .andExpect(jsonPath("$.message").value("Ponto de coleta não encontrado"));

      verify(pontoColetaService).update(id, request);
      verifyNoMoreInteractions(pontoColetaService);
      verifyNoInteractions(relatoProblemaService);
    }

    @Test
    @Order(4)
    void updateReturnsBadRequestWhenServiceThrowsBusinessException() throws Exception {
      when(pontoColetaService.update(id, request)).thenThrow(new BusinessException("Tipo inválido"));

      mockMvc
              .perform(
                      put("/api/v1/pontos-coleta/{id}", id)
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isBadRequest())
              .andExpect(jsonPath("$.message").value("Tipo inválido"));

      verify(pontoColetaService).update(id, request);
      verifyNoMoreInteractions(pontoColetaService);
      verifyNoInteractions(relatoProblemaService);
    }
  }

  @Nested
  @Order(4)
  @DisplayName("Cenários de Exclusão")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Exclusao {

    @Test
    @Order(1)
    void deleteReturnsNoContent() throws Exception {
      mockMvc.perform(delete("/api/v1/pontos-coleta/{id}", id)).andExpect(status().isNoContent());

      verify(pontoColetaService).delete(id);
      verifyNoMoreInteractions(pontoColetaService);
      verifyNoInteractions(relatoProblemaService);
    }

    @Test
    @Order(2)
    void deleteReturnsNotFoundWhenServiceThrows() throws Exception {
      doThrow(new ResourceNotFoundException("Ponto de coleta não encontrado"))
              .when(pontoColetaService)
              .delete(id);

      mockMvc
              .perform(delete("/api/v1/pontos-coleta/{id}", id))
              .andExpect(status().isNotFound())
              .andExpect(jsonPath("$.message").value("Ponto de coleta não encontrado"));

      verify(pontoColetaService).delete(id);
      verifyNoMoreInteractions(pontoColetaService);
      verifyNoInteractions(relatoProblemaService);
    }
  }

  @Nested
  @Order(5)
  @DisplayName("Cenários de Lógica Especial")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class LogicaEspecial {

    @Test
    @Order(1)
    void createRelatoProblemaReturnsCreatedResponse() throws Exception {
      RelatoProblemaRequest relatoRequest =
              new RelatoProblemaRequest(TipoRelato.LIXEIRA_CHEIA, "João Silva", "joao@email.com", "Lixeira transbordando");

      RelatoProblemaResponse relatoResponse =
              new RelatoProblemaResponse(
                      UUID.randomUUID(),
                      id,
                      response.nome(),
                      relatoRequest.tipoRelato(),
                      relatoRequest.nome(),
                      relatoRequest.email(),
                      relatoRequest.observacao(),
                      0L,
                      null,
                      null,
                      EntityStatus.ACTIVE,
                      null);

      when(relatoProblemaService.create(id, relatoRequest)).thenReturn(relatoResponse);

      mockMvc
              .perform(
                      post("/api/v1/pontos-coleta/{id}/relatos-problema", id)
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(relatoRequest)))
              .andExpect(status().isCreated())
              .andExpect(jsonPath("$.pontoColetaId").value(id.toString()))
              .andExpect(jsonPath("$.tipoRelato").value(TipoRelato.LIXEIRA_CHEIA.name()))
              .andExpect(jsonPath("$.nome").value(relatoRequest.nome()))
              .andExpect(jsonPath("$.email").value(relatoRequest.email()));

      verify(relatoProblemaService).create(id, relatoRequest);
      verifyNoMoreInteractions(relatoProblemaService);
      verifyNoInteractions(pontoColetaService);
    }

    @Test
    @Order(2)
    void createRelatoProblemaReturnsBadRequestWhenPayloadIsInvalid() throws Exception {
      // Tipo null e email inválido
      RelatoProblemaRequest relatoRequest = new RelatoProblemaRequest(null, "", "email-invalido", "");

      mockMvc
              .perform(
                      post("/api/v1/pontos-coleta/{id}/relatos-problema", id)
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(relatoRequest)))
              .andExpect(status().isBadRequest())
              .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"));

      verifyNoInteractions(pontoColetaService, relatoProblemaService);
    }
  }
}
