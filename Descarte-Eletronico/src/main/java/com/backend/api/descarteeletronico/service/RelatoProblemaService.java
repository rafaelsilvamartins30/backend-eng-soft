package com.backend.api.descarteeletronico.service;

import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.mapper.RelatoProblemaMapper;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.relato.RelatoProblema;
import com.backend.api.descarteeletronico.model.relato.dto.RelatoProblemaRequest;
import com.backend.api.descarteeletronico.model.relato.dto.RelatoProblemaResponse;
import com.backend.api.descarteeletronico.model.pontocoleta.PontoColeta;
import com.backend.api.descarteeletronico.repository.RelatoProblemaRepository;
import com.backend.api.descarteeletronico.repository.PontoColetaRepository;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RelatoProblemaService {

    private final RelatoProblemaRepository relatoProblemaRepository;
    private final PontoColetaRepository pontoColetaRepository;
    private final RelatoProblemaMapper relatoProblemaMapper;
    private final NotificacaoService notificacaoService;

    @Transactional
    public RelatoProblemaResponse create(UUID pontoColetaId, RelatoProblemaRequest request) {
        PontoColeta pontoColeta = findActivePontoColetaById(pontoColetaId);

        RelatoProblema relato = relatoProblemaMapper.toEntity(request);
        relato.setPontoColeta(pontoColeta);
        relato.setEntityStatus(EntityStatus.ACTIVE);
        relato.setDeletedAt(null);

        RelatoProblema savedRelato = relatoProblemaRepository.saveAndFlush(relato);

        notificacaoService.criarNotificacaoDeRelato(savedRelato);

        return relatoProblemaMapper.toResponse(savedRelato);
    }

    @Transactional(readOnly = true)
    public Set<RelatoProblemaResponse> findAll() {
        return relatoProblemaMapper.toResponseSet(
                relatoProblemaRepository.findAllByEntityStatusNot(EntityStatus.DELETED));
    }

    @Transactional(readOnly = true)
    public RelatoProblemaResponse findById(UUID id) {
        return relatoProblemaMapper.toResponse(findNotDeletedById(id));
    }

    @Transactional
    public void delete(UUID id) {
        RelatoProblema relato = findNotDeletedById(id);
        relato.setEntityStatus(EntityStatus.DELETED);
        relato.setDeletedAt(LocalDateTime.now());

        relatoProblemaRepository.save(relato);
    }

    private RelatoProblema findNotDeletedById(UUID id) {
        return relatoProblemaRepository
                .findByIdAndEntityStatusNot(id, EntityStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Relato de problema não encontrado"));
    }

    private PontoColeta findActivePontoColetaById(UUID id) {
        return pontoColetaRepository
                .findByIdAndEntityStatus(id, EntityStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Ponto de coleta não encontrado"));
    }
}