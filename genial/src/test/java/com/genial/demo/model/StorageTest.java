package com.genial.demo.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.genial.demo.modules.app.model.Storage;

class StorageTest {

  @Test
  void should_InitializeProductsList_When_StorageIsCreatedWithConvenienceConstructor() {
    // Given
    Storage storage = new Storage("Matriz", "Estoque principal");

    // When
    int productCount = storage.getProducts().size();

    // Then
    assertThat(storage.getProducts()).isNotNull();
    assertThat(productCount).isZero();
  }
}
