package com.backend.api.descarteeletronico.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.backend.api.descarteeletronico.exception.BusinessException;
import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.mapper.UsuarioMapper;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.usuario.Usuario;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioResponse;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioUpdateRequest;
import com.backend.api.descarteeletronico.repository.UsuarioRepository;
import java.util.Optional;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class UsuarioServiceTest {

  @Mock private UsuarioRepository usuarioRepository;

  @Mock private UsuarioMapper usuarioMapper;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private UsuarioService usuarioService;

  private UUID id;
  private Usuario usuario;
  private UsuarioResponse response;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    usuario = new Usuario("Maria Silva", "maria@descarte.com", "SenhaForte123");
    response =
        new UsuarioResponse(
            id, usuario.getNome(), usuario.getEmail(), 0L, null, null, EntityStatus.ACTIVE, null);
  }

  @Nested
  @Order(2)
  @DisplayName("Cenários de Consulta")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Consulta {

    @Test
    @Order(1)
    void findMeReturnsMappedAdminUser() {
      when(usuarioRepository.findFirstByEntityStatusOrderByCreatedAtAsc(EntityStatus.ACTIVE))
          .thenReturn(Optional.of(usuario));
      when(usuarioMapper.toResponse(usuario)).thenReturn(response);

      UsuarioResponse result = usuarioService.findMe();

      assertThat(result).isEqualTo(response);
      verify(usuarioRepository).findFirstByEntityStatusOrderByCreatedAtAsc(EntityStatus.ACTIVE);
      verify(usuarioMapper).toResponse(usuario);
      verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
      verifyNoInteractions(passwordEncoder);
    }

    @Test
    @Order(2)
    void findMeThrowsWhenAdminUserDoesNotExist() {
      when(usuarioRepository.findFirstByEntityStatusOrderByCreatedAtAsc(EntityStatus.ACTIVE))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> usuarioService.findMe())
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Usuário administrador não encontrado");
      verify(usuarioRepository).findFirstByEntityStatusOrderByCreatedAtAsc(EntityStatus.ACTIVE);
      verifyNoInteractions(usuarioMapper, passwordEncoder);
      verifyNoMoreInteractions(usuarioRepository);
    }
  }

  @Nested
  @Order(3)
  @DisplayName("Cenários de Atualização")
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Atualizacao {

    @Test
    @Order(1)
    void updateMeUpdatesProvidedFieldsEncodesPasswordAndReturnsResponse() {
      UsuarioUpdateRequest request =
          new UsuarioUpdateRequest("Admin Atualizado", "admin@descarte.local", "NovaSenha123");
      when(usuarioRepository.findFirstByEntityStatusOrderByCreatedAtAsc(EntityStatus.ACTIVE))
          .thenReturn(Optional.of(usuario));
      when(passwordEncoder.encode(request.senha())).thenReturn("senha-codificada");
      when(usuarioRepository.save(usuario)).thenReturn(usuario);
      when(usuarioMapper.toResponse(usuario)).thenReturn(response);

      UsuarioResponse result = usuarioService.updateMe(request);

      assertThat(result).isEqualTo(response);
      assertThat(usuario.getNome()).isEqualTo(request.nome());
      assertThat(usuario.getEmail()).isEqualTo(request.email());
      assertThat(usuario.getSenha()).isEqualTo("senha-codificada");
      verify(usuarioRepository).findFirstByEntityStatusOrderByCreatedAtAsc(EntityStatus.ACTIVE);
      verify(passwordEncoder).encode(request.senha());
      verify(usuarioRepository).save(usuario);
      verify(usuarioMapper).toResponse(usuario);
      verifyNoMoreInteractions(usuarioRepository, usuarioMapper, passwordEncoder);
    }

    @Test
    @Order(2)
    void updateMeIgnoresBlankFieldsAndUpdatesOnlyProvidedValues() {
      UsuarioUpdateRequest request = new UsuarioUpdateRequest("Admin Atualizado", " ", null);
      when(usuarioRepository.findFirstByEntityStatusOrderByCreatedAtAsc(EntityStatus.ACTIVE))
          .thenReturn(Optional.of(usuario));
      when(usuarioRepository.save(usuario)).thenReturn(usuario);
      when(usuarioMapper.toResponse(usuario)).thenReturn(response);

      UsuarioResponse result = usuarioService.updateMe(request);

      assertThat(result).isEqualTo(response);
      assertThat(usuario.getNome()).isEqualTo(request.nome());
      assertThat(usuario.getEmail()).isEqualTo("maria@descarte.com");
      assertThat(usuario.getSenha()).isEqualTo("SenhaForte123");
      verify(usuarioRepository).findFirstByEntityStatusOrderByCreatedAtAsc(EntityStatus.ACTIVE);
      verify(usuarioRepository).save(usuario);
      verify(usuarioMapper).toResponse(usuario);
      verifyNoInteractions(passwordEncoder);
      verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
    }

    @Test
    @Order(3)
    void updateMeThrowsWhenRequestHasNoFieldsToUpdate() {
      UsuarioUpdateRequest request = new UsuarioUpdateRequest(" ", null, "");

      assertThatThrownBy(() -> usuarioService.updateMe(request))
          .isInstanceOf(BusinessException.class)
          .hasMessage("Informe ao menos um campo para atualização.");
      verify(usuarioRepository, never())
          .findFirstByEntityStatusOrderByCreatedAtAsc(EntityStatus.ACTIVE);
      verify(usuarioRepository, never()).save(usuario);
      verifyNoInteractions(usuarioMapper, passwordEncoder);
    }

    @Test
    @Order(4)
    void updateMeThrowsWhenAdminUserDoesNotExist() {
      UsuarioUpdateRequest request = new UsuarioUpdateRequest("Admin Atualizado", null, null);
      when(usuarioRepository.findFirstByEntityStatusOrderByCreatedAtAsc(EntityStatus.ACTIVE))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> usuarioService.updateMe(request))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Usuário administrador não encontrado");
      verify(usuarioRepository).findFirstByEntityStatusOrderByCreatedAtAsc(EntityStatus.ACTIVE);
      verify(usuarioRepository, never()).save(usuario);
      verifyNoInteractions(usuarioMapper, passwordEncoder);
      verifyNoMoreInteractions(usuarioRepository);
    }
  }
}
