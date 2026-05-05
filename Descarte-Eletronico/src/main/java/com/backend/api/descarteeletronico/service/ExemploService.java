package com.backend.api.descarteeletronico.service;

import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.mapper.ExemploMapper;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.exemplo.Exemplo;
import com.backend.api.descarteeletronico.model.exemplo.dto.ExemploRequest;
import com.backend.api.descarteeletronico.model.exemplo.dto.ExemploResponse;
import com.backend.api.descarteeletronico.repository.ExemploRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExemploService implements BaseService<ExemploRequest, ExemploResponse> {

    private final ExemploRepository exemploRepository;
    private final ExemploMapper exemploMapper;

    @Override
    @Transactional
    public ExemploResponse create(ExemploRequest request) {
        Exemplo exemplo = exemploMapper.toEntity(request);
        exemplo.setEntityStatus(EntityStatus.ACTIVE);
        exemplo.setDeletedAt(null);

        Exemplo savedExemplo = exemploRepository.save(exemplo);
        return exemploMapper.toResponse(savedExemplo);
    }

    @Override
    @Transactional
    public ExemploResponse update(UUID id, ExemploRequest request) {
        Exemplo exemplo = findActiveEntityById(id);
        exemploMapper.updateEntityFromRequest(request, exemplo);

        Exemplo updatedExemplo = exemploRepository.save(exemplo);
        return exemploMapper.toResponse(updatedExemplo);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Exemplo exemplo = findActiveEntityById(id);
        exemplo.setEntityStatus(EntityStatus.DELETED);
        exemplo.setDeletedAt(LocalDateTime.now());

        exemploRepository.save(exemplo);
    }

    @Override
    @Transactional(readOnly = true)
    public ExemploResponse findById(UUID id) {
        return exemploMapper.toResponse(findActiveEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<ExemploResponse> findAll() {
        Set<Exemplo> exemplos = exemploRepository.findAllByEntityStatusNot(EntityStatus.DELETED);
        return exemploMapper.toResponseSet(exemplos);
    }

    private Exemplo findActiveEntityById(UUID id) {
        return exemploRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Exemplo não encontrado"));
    }
}
