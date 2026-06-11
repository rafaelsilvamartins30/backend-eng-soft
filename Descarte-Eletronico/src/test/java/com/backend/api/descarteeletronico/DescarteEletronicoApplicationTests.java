package com.backend.api.descarteeletronico;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.api.descarteeletronico.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

class DescarteEletronicoApplicationTests extends BaseIntegrationTest {

  @Test
  void applicationClassExists() {
    assertThat(DescarteEletronicoApplication.class).isNotNull();
  }
}
