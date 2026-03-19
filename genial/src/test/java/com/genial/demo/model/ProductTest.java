package com.genial.demo.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.genial.demo.modules.app.model.Product;

class ProductTest {

  @Test
  void should_CompareProductsById_When_EqualsIsCalled() {
    // Given
    final Product firstProduct = new Product();
    firstProduct.setId("product-1");
    final Product secondProduct = new Product();
    secondProduct.setId("product-1");

    // When
    final boolean areEqual = firstProduct.equals(secondProduct);

    // Then
    assertThat(areEqual).isTrue();
    assertThat(firstProduct.hashCode()).isEqualTo(secondProduct.hashCode());
  }

  @Test
  void should_UseBigDecimalValue_When_ProductIsCreated() {
    // Given
    final BigDecimal value = new BigDecimal("10.5000");

    // When
    final Product product = new Product("Notebook", "Ultrabook", "TECH", value, 5);

    // Then
    assertThat(product.getValue()).isEqualByComparingTo(value);
    assertThat(product.isActive()).isTrue();
  }
}
