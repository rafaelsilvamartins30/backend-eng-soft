package com.backend.api.descarteeletronico.service;

import com.backend.api.descarteeletronico.exception.ResourceNotFoundException;
import com.backend.api.descarteeletronico.mapper.ExemploMapper;
import com.backend.api.descarteeletronico.model.enums.EntityStatus;
import com.backend.api.descarteeletronico.model.exemplo.Exemplo;
import com.backend.api.descarteeletronico.model.exemplo.dto.ExemploRequest;
import com.backend.api.descarteeletronico.model.exemplo.dto.ExemploResponse;
import com.backend.api.descarteeletronico.repository.ExemploRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExemploServiceTest {

    @Mock
    private ExemploRepository exemploRepository;

    @Mock
    private ExemploMapper exemploMapper;

    @InjectMocks
    private ExemploService exemploService;

    private UUID id;
    private Exemplo exemplo;
    private ExemploRequest request;
    private ExemploResponse response;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        exemplo = new Exemplo("Coleta de notebook", "Equipamento antigo para descarte");
        request = new ExemploRequest("Coleta de notebook", "Equipamento antigo para descarte");
        response = new ExemploResponse(id, request.nome(), request.descricao(), 0L, null, null, EntityStatus.ACTIVE, null);
    }

    @Test
    void createSavesActiveEntityAndReturnsResponse() {
        when(exemploMapper.toEntity(request)).thenReturn(exemplo);
        when(exemploRepository.save(exemplo)).thenReturn(exemplo);
        when(exemploMapper.toResponse(exemplo)).thenReturn(response);

        ExemploResponse result = exemploService.create(request);

        assertThat(result).isEqualTo(response);
        assertThat(exemplo.getEntityStatus()).isEqualTo(EntityStatus.ACTIVE);
        assertThat(exemplo.getDeletedAt()).isNull();
        verify(exemploMapper).toEntity(request);
        verify(exemploRepository).save(exemplo);
        verify(exemploMapper).toResponse(exemplo);
        verifyNoMoreInteractions(exemploRepository, exemploMapper);
    }

    @Test
    void updateFindsActiveEntityAppliesMapperAndReturnsResponse() {
        when(exemploRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED)).thenReturn(Optional.of(exemplo));
        when(exemploRepository.save(exemplo)).thenReturn(exemplo);
        when(exemploMapper.toResponse(exemplo)).thenReturn(response);

        ExemploResponse result = exemploService.update(id, request);

        assertThat(result).isEqualTo(response);
        verify(exemploRepository).findByIdAndEntityStatusNot(id, EntityStatus.DELETED);
        verify(exemploMapper).updateEntityFromRequest(request, exemplo);
        verify(exemploRepository).save(exemplo);
        verify(exemploMapper).toResponse(exemplo);
        verifyNoMoreInteractions(exemploRepository, exemploMapper);
    }

    @Test
    void updateThrowsWhenEntityDoesNotExist() {
        when(exemploRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exemploService.update(id, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Exemplo não encontrado");
        verify(exemploRepository).findByIdAndEntityStatusNot(id, EntityStatus.DELETED);
        verify(exemploRepository, never()).save(exemplo);
        verifyNoInteractions(exemploMapper);
        verifyNoMoreInteractions(exemploRepository);
    }

    @Test
    void deleteMarksEntityAsDeletedAndPersistsSoftDelete() {
        when(exemploRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED)).thenReturn(Optional.of(exemplo));

        exemploService.delete(id);

        assertThat(exemplo.getEntityStatus()).isEqualTo(EntityStatus.DELETED);
        assertThat(exemplo.getDeletedAt()).isNotNull();
        verify(exemploRepository).findByIdAndEntityStatusNot(id, EntityStatus.DELETED);
        verify(exemploRepository).save(exemplo);
        verifyNoInteractions(exemploMapper);
        verifyNoMoreInteractions(exemploRepository);
    }

    @Test
    void deleteThrowsWhenEntityDoesNotExist() {
        when(exemploRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exemploService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Exemplo não encontrado");
        verify(exemploRepository).findByIdAndEntityStatusNot(id, EntityStatus.DELETED);
        verify(exemploRepository, never()).save(exemplo);
        verifyNoInteractions(exemploMapper);
        verifyNoMoreInteractions(exemploRepository);
    }

    @Test
    void findByIdReturnsMappedActiveEntity() {
        when(exemploRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED)).thenReturn(Optional.of(exemplo));
        when(exemploMapper.toResponse(exemplo)).thenReturn(response);

        ExemploResponse result = exemploService.findById(id);

        assertThat(result).isEqualTo(response);
        verify(exemploRepository).findByIdAndEntityStatusNot(id, EntityStatus.DELETED);
        verify(exemploMapper).toResponse(exemplo);
        verifyNoMoreInteractions(exemploRepository, exemploMapper);
    }

    @Test
    void findByIdThrowsWhenEntityDoesNotExist() {
        when(exemploRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exemploService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Exemplo não encontrado");
        verify(exemploRepository).findByIdAndEntityStatusNot(id, EntityStatus.DELETED);
        verifyNoInteractions(exemploMapper);
        verifyNoMoreInteractions(exemploRepository);
    }

    @Test
    void findAllIgnoresDeletedEntitiesAndMapsResponses() {
        Set<Exemplo> exemplos = Set.of(exemplo);
        Set<ExemploResponse> responses = Set.of(response);
        when(exemploRepository.findAllByEntityStatusNot(EntityStatus.DELETED)).thenReturn(exemplos);
        when(exemploMapper.toResponseSet(exemplos)).thenReturn(responses);

        Set<ExemploResponse> result = exemploService.findAll();

        assertThat(result).isEqualTo(responses);
        verify(exemploRepository).findAllByEntityStatusNot(EntityStatus.DELETED);
        verify(exemploMapper).toResponseSet(exemplos);
        verifyNoMoreInteractions(exemploRepository, exemploMapper);
    }

    @Test
    void findAllWithEmptyResultDelegatesToMapper() {
        Set<Exemplo> exemplos = Set.of();
        Set<ExemploResponse> responses = Set.of();
        when(exemploRepository.findAllByEntityStatusNot(EntityStatus.DELETED)).thenReturn(exemplos);
        when(exemploMapper.toResponseSet(exemplos)).thenReturn(responses);

        Set<ExemploResponse> result = exemploService.findAll();

        assertThat(result).isEmpty();
        verify(exemploRepository).findAllByEntityStatusNot(EntityStatus.DELETED);
        verify(exemploMapper).toResponseSet(exemplos);
        verifyNoMoreInteractions(exemploRepository, exemploMapper);
    }
}
