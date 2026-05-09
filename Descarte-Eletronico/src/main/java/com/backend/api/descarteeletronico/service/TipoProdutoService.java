package com.backend.api.descarteeletronico.service;

import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.mapper.TipoProdutoMapper;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.tipoproduto.TipoProduto;
import com.backend.api.descarteeletronico.model.tipoproduto.dto.TipoProdutoRequest;
import com.backend.api.descarteeletronico.model.tipoproduto.dto.TipoProdutoResponse;
import com.backend.api.descarteeletronico.repository.TipoProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TipoProdutoService implements BaseService<TipoProdutoRequest, TipoProdutoResponse> {

  private final TipoProdutoRepository tipoProdutoRepository;
  private final TipoProdutoMapper tipoProdutoMapper;

  @Override
  @Transactional
  public TipoProdutoResponse create(TipoProdutoRequest request) {
    TipoProduto tipoProduto = tipoProdutoMapper.toEntity(request);
    tipoProduto.setEntityStatus(EntityStatus.ACTIVE);
    tipoProduto.setDeletedAt(null);

    TipoProduto savedTipoProduto = tipoProdutoRepository.save(tipoProduto);
    return tipoProdutoMapper.toResponse(savedTipoProduto);
  }

  @Override
  @Transactional
  public TipoProdutoResponse update(UUID id, TipoProdutoRequest request) {
    TipoProduto tipoProduto = findActiveEntityById(id);
    tipoProdutoMapper.updateEntityFromRequest(request, tipoProduto);

    TipoProduto updatedTipoProduto = tipoProdutoRepository.save(tipoProduto);
    return tipoProdutoMapper.toResponse(updatedTipoProduto);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    TipoProduto tipoProduto = findActiveEntityById(id);
    tipoProduto.setEntityStatus(EntityStatus.DELETED);
    tipoProduto.setDeletedAt(LocalDateTime.now());

    tipoProdutoRepository.save(tipoProduto);
  }

  @Override
  @Transactional(readOnly = true)
  public TipoProdutoResponse findById(UUID id) {
    return tipoProdutoMapper.toResponse(findActiveEntityById(id));
  }

  @Override
  @Transactional(readOnly = true)
  public Set<TipoProdutoResponse> findAll() {
    Set<TipoProduto> tiposProduto =
        tipoProdutoRepository.findAllByEntityStatusNot(EntityStatus.DELETED);
    return tipoProdutoMapper.toResponseSet(tiposProduto);
  }

  private TipoProduto findActiveEntityById(UUID id) {
    return tipoProdutoRepository
        .findByIdAndEntityStatusNot(id, EntityStatus.DELETED)
        .orElseThrow(() -> new ResourceNotFoundException("Tipo de produto não encontrado"));
  }
}
