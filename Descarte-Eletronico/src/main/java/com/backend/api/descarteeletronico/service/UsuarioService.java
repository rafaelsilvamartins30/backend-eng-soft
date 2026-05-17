package com.backend.api.descarteeletronico.service;

import com.backend.api.descarteeletronico.exception.BusinessException;
import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.mapper.UsuarioMapper;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.usuario.Usuario;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioResponse;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioUpdateRequest;
import com.backend.api.descarteeletronico.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UsuarioService {

  private final UsuarioRepository usuarioRepository;
  private final UsuarioMapper usuarioMapper;
  private final PasswordEncoder passwordEncoder;

  @Transactional(readOnly = true)
  public UsuarioResponse findMe() {
    return usuarioMapper.toResponse(findAdminUsuario());
  }

  @Transactional
  public UsuarioResponse updateMe(UsuarioUpdateRequest request) {
    validateAtLeastOneField(request);

    Usuario usuario = findAdminUsuario();
    updateIfPresent(usuario, request);

    Usuario updatedUsuario = usuarioRepository.save(usuario);
    return usuarioMapper.toResponse(updatedUsuario);
  }

  private void validateAtLeastOneField(UsuarioUpdateRequest request) {
    if (!StringUtils.hasText(request.nome())
        && !StringUtils.hasText(request.email())
        && !StringUtils.hasText(request.senha())) {
      throw new BusinessException("Informe ao menos um campo para atualização.");
    }
  }

  private void updateIfPresent(Usuario usuario, UsuarioUpdateRequest request) {
    if (StringUtils.hasText(request.nome())) {
      usuario.setNome(request.nome());
    }

    if (StringUtils.hasText(request.email())) {
      usuario.setEmail(request.email());
    }

    if (StringUtils.hasText(request.senha())) {
      usuario.setSenha(passwordEncoder.encode(request.senha()));
    }
  }

  private Usuario findAdminUsuario() {
    return usuarioRepository
        .findFirstByEntityStatusOrderByCreatedAtAsc(EntityStatus.ACTIVE)
        .orElseThrow(() -> new ResourceNotFoundException("Usuário administrador não encontrado"));
  }
}
