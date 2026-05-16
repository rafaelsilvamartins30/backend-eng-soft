package com.backend.api.descarteeletronico.service;

import com.backend.api.descarteeletronico.exception.BusinessException;
import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.mapper.UsuarioMapper;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.usuario.Usuario;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioRequest;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioResponse;
import com.backend.api.descarteeletronico.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService implements BaseService<UsuarioRequest, UsuarioResponse> {

    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;

    @Override
    @Transactional
    public UsuarioResponse create(UsuarioRequest request) {
        if (repository.existsByEmailAndEntityStatusNot(request.email(), EntityStatus.DELETED)) {
            throw new BusinessException("Já existe um usuário ativo cadastrado com este e-mail.");
        }

        Usuario usuario = mapper.toEntity(request);

        usuario.setEntityStatus(EntityStatus.ACTIVE);
        usuario.setDeletedAt(null);

        Usuario savedUsuario = repository.save(usuario);
        return mapper.toResponse(savedUsuario);
    }

    @Override
    @Transactional
    public UsuarioResponse update(UUID id, UsuarioRequest request) {
        Usuario usuario = buscarUsuarioAtivo(id);

        if (!usuario.getEmail().equals(request.email()) &&
                repository.existsByEmailAndEntityStatusNot(request.email(), EntityStatus.DELETED)) {
            throw new BusinessException("Já existe outro usuário cadastrado com este e-mail.");
        }

        mapper.updateEntityFromRequest(request, usuario);

        Usuario updatedUsuario = repository.save(usuario);
        return mapper.toResponse(updatedUsuario);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Usuario usuario = buscarUsuarioAtivo(id);

        usuario.setEntityStatus(EntityStatus.DELETED);
        usuario.setDeletedAt(LocalDateTime.now());

        repository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse findById(UUID id) {
        return mapper.toResponse(buscarUsuarioAtivo(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<UsuarioResponse> findAll() {
        return repository.findAllByEntityStatusNot(EntityStatus.DELETED)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toSet());
    }

    private Usuario buscarUsuarioAtivo(UUID id) {
        return repository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }
}