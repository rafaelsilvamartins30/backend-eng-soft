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
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioRequest;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioResponse;
import com.backend.api.descarteeletronico.repository.UsuarioRepository;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

  @Mock private UsuarioRepository usuarioRepository;

  @Mock private UsuarioMapper usuarioMapper;

  @InjectMocks private UsuarioService usuarioService;

  private UUID id;
  private Usuario usuario;
  private UsuarioRequest request;
  private UsuarioResponse response;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    usuario = new Usuario("Maria Silva", "maria@descarte.com", "SenhaForte123");
    request = new UsuarioRequest(usuario.getNome(), usuario.getEmail(), usuario.getSenha());
    response =
        new UsuarioResponse(
            id, request.nome(), request.email(), 0L, null, null, EntityStatus.ACTIVE, null);
  }

  @Test
  void createSavesActiveEntityAndReturnsResponse() {
    when(usuarioRepository.existsByEmailAndEntityStatus(request.email(), EntityStatus.ACTIVE))
        .thenReturn(false);
    when(usuarioMapper.toEntity(request)).thenReturn(usuario);
    when(usuarioRepository.save(usuario)).thenReturn(usuario);
    when(usuarioMapper.toResponse(usuario)).thenReturn(response);

    UsuarioResponse result = usuarioService.create(request);

    assertThat(result).isEqualTo(response);
    assertThat(usuario.getEntityStatus()).isEqualTo(EntityStatus.ACTIVE);
    assertThat(usuario.getDeletedAt()).isNull();
    verify(usuarioRepository).existsByEmailAndEntityStatus(request.email(), EntityStatus.ACTIVE);
    verify(usuarioMapper).toEntity(request);
    verify(usuarioRepository).save(usuario);
    verify(usuarioMapper).toResponse(usuario);
    verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
  }

  @Test
  void createThrowsWhenActiveEmailAlreadyExists() {
    when(usuarioRepository.existsByEmailAndEntityStatus(request.email(), EntityStatus.ACTIVE))
        .thenReturn(true);

    assertThatThrownBy(() -> usuarioService.create(request))
        .isInstanceOf(BusinessException.class)
        .hasMessage("Já existe um usuário ativo cadastrado com este e-mail.");
    verify(usuarioRepository).existsByEmailAndEntityStatus(request.email(), EntityStatus.ACTIVE);
    verify(usuarioRepository, never()).save(usuario);
    verifyNoInteractions(usuarioMapper);
    verifyNoMoreInteractions(usuarioRepository);
  }

  @Test
  void updateFindsActiveEntityAppliesMapperAndReturnsResponse() {
    when(usuarioRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
        .thenReturn(Optional.of(usuario));
    when(usuarioRepository.save(usuario)).thenReturn(usuario);
    when(usuarioMapper.toResponse(usuario)).thenReturn(response);

    UsuarioResponse result = usuarioService.update(id, request);

    assertThat(result).isEqualTo(response);
    verify(usuarioRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
    verify(usuarioMapper).updateEntityFromRequest(request, usuario);
    verify(usuarioRepository).save(usuario);
    verify(usuarioMapper).toResponse(usuario);
    verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
  }

  @Test
  void updateThrowsWhenAnotherActiveUserHasSameEmail() {
    UsuarioRequest requestWithNewEmail =
        new UsuarioRequest(usuario.getNome(), "duplicado@descarte.com", usuario.getSenha());
    when(usuarioRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
        .thenReturn(Optional.of(usuario));
    when(usuarioRepository.existsByEmailAndEntityStatus(
            requestWithNewEmail.email(), EntityStatus.ACTIVE))
        .thenReturn(true);

    assertThatThrownBy(() -> usuarioService.update(id, requestWithNewEmail))
        .isInstanceOf(BusinessException.class)
        .hasMessage("Já existe outro usuário cadastrado com este e-mail.");
    verify(usuarioRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
    verify(usuarioRepository)
        .existsByEmailAndEntityStatus(requestWithNewEmail.email(), EntityStatus.ACTIVE);
    verify(usuarioRepository, never()).save(usuario);
    verifyNoInteractions(usuarioMapper);
    verifyNoMoreInteractions(usuarioRepository);
  }

  @Test
  void updateThrowsWhenEntityDoesNotExist() {
    when(usuarioRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> usuarioService.update(id, request))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Usuário não encontrado");
    verify(usuarioRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
    verify(usuarioRepository, never()).save(usuario);
    verifyNoInteractions(usuarioMapper);
    verifyNoMoreInteractions(usuarioRepository);
  }

  @Test
  void deleteMarksEntityAsDeletedAndPersistsSoftDelete() {
    when(usuarioRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
        .thenReturn(Optional.of(usuario));

    usuarioService.delete(id);

    assertThat(usuario.getEntityStatus()).isEqualTo(EntityStatus.DELETED);
    assertThat(usuario.getDeletedAt()).isNotNull();
    verify(usuarioRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
    verify(usuarioRepository).save(usuario);
    verifyNoInteractions(usuarioMapper);
    verifyNoMoreInteractions(usuarioRepository);
  }

  @Test
  void deleteThrowsWhenEntityDoesNotExist() {
    when(usuarioRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> usuarioService.delete(id))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Usuário não encontrado");
    verify(usuarioRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
    verify(usuarioRepository, never()).save(usuario);
    verifyNoInteractions(usuarioMapper);
    verifyNoMoreInteractions(usuarioRepository);
  }

  @Test
  void findByIdReturnsMappedActiveEntity() {
    when(usuarioRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
        .thenReturn(Optional.of(usuario));
    when(usuarioMapper.toResponse(usuario)).thenReturn(response);

    UsuarioResponse result = usuarioService.findById(id);

    assertThat(result).isEqualTo(response);
    verify(usuarioRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
    verify(usuarioMapper).toResponse(usuario);
    verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
  }

  @Test
  void findByIdThrowsWhenEntityDoesNotExist() {
    when(usuarioRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> usuarioService.findById(id))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Usuário não encontrado");
    verify(usuarioRepository).findByIdAndEntityStatus(id, EntityStatus.ACTIVE);
    verifyNoInteractions(usuarioMapper);
    verifyNoMoreInteractions(usuarioRepository);
  }

  @Test
  void findAllReturnsOnlyActiveEntitiesAndMapsResponses() {
    Set<Usuario> usuarios = Set.of(usuario);
    Set<UsuarioResponse> responses = Set.of(response);
    when(usuarioRepository.findAllByEntityStatus(EntityStatus.ACTIVE)).thenReturn(usuarios);
    when(usuarioMapper.toResponseSet(usuarios)).thenReturn(responses);

    Set<UsuarioResponse> result = usuarioService.findAll();

    assertThat(result).isEqualTo(responses);
    verify(usuarioRepository).findAllByEntityStatus(EntityStatus.ACTIVE);
    verify(usuarioMapper).toResponseSet(usuarios);
    verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
  }

  @Test
  void findAllWithEmptyResultDelegatesToMapper() {
    Set<Usuario> usuarios = Set.of();
    Set<UsuarioResponse> responses = Set.of();
    when(usuarioRepository.findAllByEntityStatus(EntityStatus.ACTIVE)).thenReturn(usuarios);
    when(usuarioMapper.toResponseSet(usuarios)).thenReturn(responses);

    Set<UsuarioResponse> result = usuarioService.findAll();

    assertThat(result).isEmpty();
    verify(usuarioRepository).findAllByEntityStatus(EntityStatus.ACTIVE);
    verify(usuarioMapper).toResponseSet(usuarios);
    verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
  }
}
