package com.backend.api.descarteeletronico.mapper;

import com.backend.api.descarteeletronico.model.relato.RelatoProblema;
import com.backend.api.descarteeletronico.model.relato.dto.RelatoProblemaRequest;
import com.backend.api.descarteeletronico.model.relato.dto.RelatoProblemaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RelatoProblemaMapper extends BaseMapper<RelatoProblema, RelatoProblemaRequest, RelatoProblemaResponse> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "entityStatus", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "pontoColeta", ignore = true)
    RelatoProblema toEntity(RelatoProblemaRequest request);

    @Override
    @Mapping(target = "pontoColetaId", source = "pontoColeta.id")
    @Mapping(target = "pontoColetaNome", source = "pontoColeta.nome")
    RelatoProblemaResponse toResponse(RelatoProblema entity);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "entityStatus", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "pontoColeta", ignore = true)
    void updateEntityFromRequest(RelatoProblemaRequest request, @MappingTarget RelatoProblema entity);
}