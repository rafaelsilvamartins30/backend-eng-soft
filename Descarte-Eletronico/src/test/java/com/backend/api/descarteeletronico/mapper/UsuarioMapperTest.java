package com.backend.api.descarteeletronico.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.model.usuario.Usuario;
import com.backend.api.descarteeletronico.model.usuario.dto.UsuarioResponse;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UsuarioMapperTest {

  private final UsuarioMapper mapper = Mappers.getMapper(UsuarioMapper.class);

  @Test
  void toResponseMapsUsuarioToResponse() {
    Usuario usuario = new Usuario("Admin", "admin@test.com", "password");
    usuario.setRoles(Set.of());

    UsuarioResponse response = mapper.toResponse(usuario);

    assertThat(response.nome()).isEqualTo(usuario.getNome());
    assertThat(response.email()).isEqualTo(usuario.getEmail());
  }
}
