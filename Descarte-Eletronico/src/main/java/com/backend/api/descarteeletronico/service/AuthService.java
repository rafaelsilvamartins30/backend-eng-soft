package com.backend.api.descarteeletronico.service;

import com.backend.api.descarteeletronico.mapper.UsuarioMapper;
import com.backend.api.descarteeletronico.model.auth.dto.LoginRequest;
import com.backend.api.descarteeletronico.model.auth.dto.LoginResponse;
import com.backend.api.descarteeletronico.model.usuario.Usuario;
import com.backend.api.descarteeletronico.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UsuarioMapper usuarioMapper;

  public LoginResponse login(LoginRequest request) {
    var authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.senha()));

    Usuario usuario = (Usuario) authentication.getPrincipal();
    String accessToken = jwtService.generateToken(usuario);

    return new LoginResponse(
        accessToken, "Bearer", jwtService.expiresInSeconds(), usuarioMapper.toResponse(usuario));
  }
}
