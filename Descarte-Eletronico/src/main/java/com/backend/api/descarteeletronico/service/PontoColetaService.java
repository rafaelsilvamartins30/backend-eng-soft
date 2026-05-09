package com.backend.api.descarteeletronico.service;

import com.backend.api.descarteeletronico.exception.BusinessException;
import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.mapper.PontoColetaMapper;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import com.backend.api.descarteeletronico.model.pontocoleta.dto.PontoColetaRequest;
import com.backend.api.descarteeletronico.model.pontocoleta.dto.PontoColetaResponse;
import com.backend.api.descarteeletronico.model.tipoproduto.TipoProduto;
import com.backend.api.descarteeletronico.repository.PontoColetaRepository;
import com.backend.api.descarteeletronico.repository.TipoProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PontoColetaService implements BaseService<PontoColetaRequest, PontoColetaResponse> {

  private final PontoColetaRepository pontoColetaRepository;
  private final TipoProdutoRepository tipoProdutoRepository;
  private final PontoColetaMapper pontoColetaMapper;

  @Override
  @Transactional
  public PontoColetaResponse create(PontoColetaRequest request) {
    PontoColeta pontoColeta = pontoColetaMapper.toEntity(request);
    pontoColeta.setTiposProduto(findActiveTiposProduto(request.tipoProdutoIds()));
    pontoColeta.setEntityStatus(EntityStatus.ACTIVE);
    pontoColeta.setDeletedAt(null);

    PontoColeta savedPontoColeta = pontoColetaRepository.save(pontoColeta);
    return pontoColetaMapper.toResponse(savedPontoColeta);
  }

  @Override
  @Transactional
  public PontoColetaResponse update(UUID id, PontoColetaRequest request) {
    PontoColeta pontoColeta = findActiveEntityById(id);
    pontoColetaMapper.updateEntityFromRequest(request, pontoColeta);
    pontoColeta.setTiposProduto(findActiveTiposProduto(request.tipoProdutoIds()));

    PontoColeta updatedPontoColeta = pontoColetaRepository.save(pontoColeta);
    return pontoColetaMapper.toResponse(updatedPontoColeta);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    PontoColeta pontoColeta = findActiveEntityById(id);
    pontoColeta.setEntityStatus(EntityStatus.DELETED);
    pontoColeta.setDeletedAt(LocalDateTime.now());

    pontoColetaRepository.save(pontoColeta);
  }

  @Override
  @Transactional(readOnly = true)
  public PontoColetaResponse findById(UUID id) {
    return pontoColetaMapper.toResponse(findActiveEntityById(id));
  }

  @Override
  @Transactional(readOnly = true)
  public Set<PontoColetaResponse> findAll() {
    Set<PontoColeta> pontosColeta =
        pontoColetaRepository.findAllByEntityStatusNot(EntityStatus.DELETED);
    return pontoColetaMapper.toResponseSet(pontosColeta);
  }

  private PontoColeta findActiveEntityById(UUID id) {
    return pontoColetaRepository
        .findByIdAndEntityStatusNot(id, EntityStatus.DELETED)
        .orElseThrow(() -> new ResourceNotFoundException("Ponto de coleta não encontrado"));
  }

  private Set<TipoProduto> findActiveTiposProduto(Set<UUID> ids) {
    Set<TipoProduto> tiposProduto =
        tipoProdutoRepository.findAllByIdInAndEntityStatusNot(ids, EntityStatus.DELETED);

    if (tiposProduto.size() != ids.size()) {
      throw new BusinessException("Informe apenas tipos de produto ativos e existentes");
    }

    return new LinkedHashSet<>(tiposProduto);
  }
}
