package com.backend.api.descarteeletronico.service;

import com.backend.api.descarteeletronico.exception.BusinessException;
import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.mapper.UsuarioMapper;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.usuario.Usuario;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioResponse;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioUpdateRequest;
import com.backend.api.descarteeletronico.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

  @Mock private UsuarioRepository usuarioRepository;

  @Mock private UsuarioMapper usuarioMapper;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private UsuarioService usuarioService;

  private Usuario usuario;
  private UsuarioResponse response;

  @BeforeEach
  void setUp() {
    UUID id = UUID.randomUUID();
    usuario = new Usuario();
    usuario.setNome("Administrador");
    usuario.setEmail("admin@descarte.local");
    usuario.setSenha("senha-codificada");
    usuario.setEntityStatus(EntityStatus.ACTIVE);
    response =
        new UsuarioResponse(
            id, usuario.getNome(), usuario.getEmail(), Set.of(), EntityStatus.ACTIVE);
  }

  @Test
  void findMeReturnsMappedAdminUser() {
    when(usuarioRepository.findFirstByEntityStatusOrderByCreatedAtAsc(EntityStatus.ACTIVE))
        .thenReturn(Optional.of(usuario));
    when(usuarioMapper.toResponse(usuario)).thenReturn(response);

    UsuarioResponse result = usuarioService.findMe();

    assertThat(result).isEqualTo(response);
    verify(usuarioRepository).findFirstByEntityStatusOrderByCreatedAtAsc(EntityStatus.ACTIVE);
    verify(usuarioMapper).toResponse(usuario);
    verifyNoMoreInteractions(usuarioRepository, usuarioMapper, passwordEncoder);
  }

  @Test
  void findMeThrowsWhenAdminUserDoesNotExist() {
    when(usuarioRepository.findFirstByEntityStatusOrderByCreatedAtAsc(EntityStatus.ACTIVE))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> usuarioService.findMe())
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Usuário administrador não encontrado");
    verify(usuarioRepository).findFirstByEntityStatusOrderByCreatedAtAsc(EntityStatus.ACTIVE);
    verifyNoMoreInteractions(usuarioRepository, usuarioMapper, passwordEncoder);
  }

  @Test
  void updateMeUpdatesProvidedFieldsAndEncodesPassword() {
    UsuarioUpdateRequest request =
        new UsuarioUpdateRequest("Admin Atualizado", "novo@descarte.local", "Admin@456");
    when(usuarioRepository.findFirstByEntityStatusOrderByCreatedAtAsc(EntityStatus.ACTIVE))
        .thenReturn(Optional.of(usuario));
    when(passwordEncoder.encode(request.senha())).thenReturn("nova-senha-codificada");
    when(usuarioRepository.save(usuario)).thenReturn(usuario);
    when(usuarioMapper.toResponse(usuario)).thenReturn(response);

    UsuarioResponse result = usuarioService.updateMe(request);

    assertThat(result).isEqualTo(response);
    assertThat(usuario.getNome()).isEqualTo(request.nome());
    assertThat(usuario.getEmail()).isEqualTo(request.email());
    assertThat(usuario.getSenha()).isEqualTo("nova-senha-codificada");
    verify(usuarioRepository).findFirstByEntityStatusOrderByCreatedAtAsc(EntityStatus.ACTIVE);
    verify(passwordEncoder).encode(request.senha());
    verify(usuarioRepository).save(usuario);
    verify(usuarioMapper).toResponse(usuario);
    verifyNoMoreInteractions(usuarioRepository, usuarioMapper, passwordEncoder);
  }

  @Test
  void updateMeThrowsWhenRequestHasNoFields() {
    UsuarioUpdateRequest request = new UsuarioUpdateRequest(null, null, null);

    assertThatThrownBy(() -> usuarioService.updateMe(request))
        .isInstanceOf(BusinessException.class)
        .hasMessage("Informe ao menos um campo para atualização.");
    verify(usuarioRepository, never()).save(usuario);
    verifyNoMoreInteractions(usuarioRepository, usuarioMapper, passwordEncoder);
  }
}
