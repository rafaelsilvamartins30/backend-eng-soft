package com.backend.api.descarteeletronico.service;

import com.backend.api.descarteeletronico.exception.BusinessException;
import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.mapper.UsuarioMapper;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.usuario.Usuario;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioRequest;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioResponse;
import com.backend.api.descarteeletronico.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService implements BaseService<UsuarioRequest, UsuarioResponse> {

  private final UsuarioRepository usuarioRepository;
  private final UsuarioMapper usuarioMapper;

  @Override
  @Transactional
  public UsuarioResponse create(UsuarioRequest request) {
    if (usuarioRepository.existsByEmailAndEntityStatus(request.email(), EntityStatus.ACTIVE)) {
      throw new BusinessException("Já existe um usuário ativo cadastrado com este e-mail.");
    }

    Usuario usuario = usuarioMapper.toEntity(request);
    usuario.setEntityStatus(EntityStatus.ACTIVE);
    usuario.setDeletedAt(null);

    Usuario savedUsuario = usuarioRepository.save(usuario);
    return usuarioMapper.toResponse(savedUsuario);
  }

  @Override
  @Transactional
  public UsuarioResponse update(UUID id, UsuarioRequest request) {
    Usuario usuario = findActiveEntityById(id);

    if (!usuario.getEmail().equals(request.email())
        && usuarioRepository.existsByEmailAndEntityStatus(request.email(), EntityStatus.ACTIVE)) {
      throw new BusinessException("Já existe outro usuário cadastrado com este e-mail.");
    }

    usuarioMapper.updateEntityFromRequest(request, usuario);

    Usuario updatedUsuario = usuarioRepository.save(usuario);
    return usuarioMapper.toResponse(updatedUsuario);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    Usuario usuario = findActiveEntityById(id);
    usuario.setEntityStatus(EntityStatus.DELETED);
    usuario.setDeletedAt(LocalDateTime.now());

    usuarioRepository.save(usuario);
  }

  @Override
  @Transactional(readOnly = true)
  public UsuarioResponse findById(UUID id) {
    return usuarioMapper.toResponse(findActiveEntityById(id));
  }

  @Override
  @Transactional(readOnly = true)
  public Set<UsuarioResponse> findAll() {
    Set<Usuario> usuarios = usuarioRepository.findAllByEntityStatus(EntityStatus.ACTIVE);
    return usuarioMapper.toResponseSet(usuarios);
  }

  private Usuario findActiveEntityById(UUID id) {
    return usuarioRepository
        .findByIdAndEntityStatus(id, EntityStatus.ACTIVE)
        .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
  }
}
