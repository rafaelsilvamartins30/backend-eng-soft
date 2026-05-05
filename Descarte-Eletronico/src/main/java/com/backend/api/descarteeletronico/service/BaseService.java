package com.backend.api.descarteeletronico.service;

import java.util.Set;
import java.util.UUID;

public interface BaseService<REQUEST, RESPONSE> {

    /**
     * Cria uma nova entidade.
     *
     * @param request os dados da entidade a ser criada
     * @return a entidade criada
     */
    RESPONSE create(REQUEST request);

    /**
     * Atualiza uma entidade existente.
     *
     * @param id o ID da entidade a ser atualizada
     * @param request os novos dados da entidade
     * @return a entidade atualizada
     */
    RESPONSE update(UUID id, REQUEST request);

    /**
     * Aplica soft delete em uma entidade pelo seu ID.
     *
     * @param id o ID da entidade a ser deletada
     */
    void delete(UUID id);

    /**
     * Busca uma entidade pelo seu ID.
     *
     * @param id o ID da entidade a ser buscada
     * @return a entidade encontrada
     */
    RESPONSE findById(UUID id);

    /**
     * Busca todas as entidades.
     *
     * @return um conjunto contendo todas as entidades
     */
    Set<RESPONSE> findAll();
}
