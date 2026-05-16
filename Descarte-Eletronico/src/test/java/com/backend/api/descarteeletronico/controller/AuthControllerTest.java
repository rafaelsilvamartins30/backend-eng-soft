package com.backend.api.descarteeletronico.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.api.descarteeletronico.exception.GlobalExceptionHandler;
import com.backend.api.descarteeletronico.model.auth.dto.LoginRequest;
import com.backend.api.descarteeletronico.model.auth.dto.LoginResponse;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioResponse;
import com.backend.api.descarteeletronico.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AuthControllerTest {

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private AuthService authService;
  private LoginRequest request;
  private LoginResponse response;

  @BeforeEach
  void setUp() {
    authService = mock(AuthService.class);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new AuthController(authService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    objectMapper = new ObjectMapper();

    request = new LoginRequest("admin@descarte.local", "Admin@123");
    response =
        new LoginResponse(
            "jwt-token",
            "Bearer",
            3600L,
            new UsuarioResponse(
                UUID.randomUUID(),
                "Administrador",
                request.email(),
                0L,
                null,
                null,
                EntityStatus.ACTIVE,
                null));
  }

  @Test
  void loginReturnsTokenWhenCredentialsAreValid() throws Exception {
    when(authService.login(request)).thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value(response.accessToken()))
        .andExpect(jsonPath("$.tokenType").value("Bearer"))
        .andExpect(jsonPath("$.expiresIn").value(3600L))
        .andExpect(jsonPath("$.usuario.email").value(request.email()));

    verify(authService).login(request);
    verifyNoMoreInteractions(authService);
  }

  @Test
  void loginReturnsUnauthorizedWhenCredentialsAreInvalid() throws Exception {
    when(authService.login(request))
        .thenThrow(new BadCredentialsException("Credenciais inválidas"));

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Credenciais inválidas"));

    verify(authService).login(request);
    verifyNoMoreInteractions(authService);
  }

  @Test
  void loginReturnsBadRequestWhenPayloadIsInvalid() throws Exception {
    LoginRequest invalidRequest = new LoginRequest("email-invalido", "");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"));

    verifyNoInteractions(authService);
  }
}
