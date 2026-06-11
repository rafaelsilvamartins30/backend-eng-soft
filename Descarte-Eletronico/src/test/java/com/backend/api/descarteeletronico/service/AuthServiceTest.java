package com.backend.api.descarteeletronico.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.backend.api.descarteeletronico.mapper.UsuarioMapper;
import com.backend.api.descarteeletronico.model.auth.dto.LoginRequest;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.usuario.Usuario;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioResponse;
import com.backend.api.descarteeletronico.security.JwtService;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class AuthServiceTest {

  private AuthenticationManager authenticationManager;
  private JwtService jwtService;
  private UsuarioMapper usuarioMapper;
  private AuthService authService;

  @BeforeEach
  void setUp() {
    authenticationManager = mock(AuthenticationManager.class);
    jwtService = mock(JwtService.class);
    usuarioMapper = mock(UsuarioMapper.class);
    authService = new AuthService(authenticationManager, jwtService, usuarioMapper);
  }

  @Nested
  @Order(1)
  @DisplayName("Cenários de Lógica Especial")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class LoginTests {

    @Test
    @Order(1)
    void loginAuthenticatesUserAndReturnsTokenResponse() {
      LoginRequest request = new LoginRequest("admin@descarte.local", "Admin@123");
      Usuario usuario = new Usuario("Administrador", request.email(), "encoded-password");
      UsuarioResponse usuarioResponse =
          new UsuarioResponse(
              UUID.randomUUID(),
              usuario.getNome(),
              usuario.getEmail(),
              0L,
              null,
              null,
              EntityStatus.ACTIVE,
              null);
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

      when(authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(request.email(), request.senha())))
          .thenReturn(authentication);
      when(jwtService.generateToken(usuario)).thenReturn("jwt-token");
      when(jwtService.expiresInSeconds()).thenReturn(3600L);
      when(usuarioMapper.toResponse(usuario)).thenReturn(usuarioResponse);

      var response = authService.login(request);

      assertThat(response.accessToken()).isEqualTo("jwt-token");
      assertThat(response.tokenType()).isEqualTo("Bearer");
      assertThat(response.expiresIn()).isEqualTo(3600L);
      assertThat(response.usuario()).isEqualTo(usuarioResponse);

      verify(authenticationManager)
          .authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.senha()));
      verify(jwtService).generateToken(usuario);
      verify(jwtService).expiresInSeconds();
      verify(usuarioMapper).toResponse(usuario);
      verifyNoMoreInteractions(authenticationManager, jwtService, usuarioMapper);
    }
  }
}
