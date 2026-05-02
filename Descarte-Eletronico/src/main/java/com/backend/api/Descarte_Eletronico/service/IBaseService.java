package com.backend.api.Descarte_Eletronico.service;

import com.backend.api.Descarte_Eletronico.model.Entity.BaseEntity;
import com.backend.api.Descarte_Eletronico.model.enums.Status;

import java.util.UUID;

public interface IBaseService<T extends BaseEntity> {

    /**
     * Cria ou atualiza uma entidade
     *
     * @param entity a entidade a ser criada ou atualizada
     * @return a entidade criada ou atualizada
     */
    T createOrUpdate(T entity);

    /**
     * Verifica se uma entidade está inativa
     *
     * @param id o ID da entidade
     * @return true se a entidade está inativa, false caso contrário
     */
    boolean verificarInativo(UUID id);

    /**
     * Verifica relações de uma entidade antes de deletá-la
     *
     * @param id o ID da entidade
     * @return true se não há relações que impeçam a exclusão, false caso contrário
     */
    boolean verificarRelacao(UUID id);

    /**
     * Realiza soft delete (marca como inativo) de uma entidade
     *
     * @param id o ID da entidade a ser deletada
     * @return true se o soft delete foi realizado com sucesso, false caso contrário
     */
    boolean softDelete(UUID id);

}
