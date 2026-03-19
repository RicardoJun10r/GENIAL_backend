package com.genial.demo.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import com.genial.demo.modules.app.model.Product;
import com.genial.demo.modules.app.model.Storage;
import com.genial.demo.modules.app.model.User;
import com.genial.demo.modules.app.repositories.ProductRepository;
import com.genial.demo.modules.app.repositories.StorageRepository;
import com.genial.demo.modules.app.repositories.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:auditingdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false;NON_KEYWORDS=VALUE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class EntityAuditingTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private StorageRepository storageRepository;

  @Autowired
  private ProductRepository productRepository;

  @Test
  void should_PopulateAuditFields_When_EntitiesArePersisted() {
    // Given
    final User user = new User("john@genial.com", "John", "hashed-password");
    final User savedUser = userRepository.saveAndFlush(user);

    final Storage storage = new Storage("Matriz", "Estoque principal");
    storage.setUser(savedUser);
    final Storage savedStorage = storageRepository.saveAndFlush(storage);

    final Product product = new Product(
            "Notebook",
            "Ultrabook",
            "TECH",
            new BigDecimal("10.5000"),
            5);
    product.setStorage(savedStorage);

    // When
    final Product savedProduct = productRepository.saveAndFlush(product);

    // Then
    assertThat(savedUser.getCreatedAt()).isNotNull();
    assertThat(savedUser.getUpdatedAt()).isNotNull();
    assertThat(savedStorage.getCreatedAt()).isNotNull();
    assertThat(savedStorage.getUpdatedAt()).isNotNull();
    assertThat(savedProduct.getCreatedAt()).isNotNull();
    assertThat(savedProduct.getUpdatedAt()).isNotNull();
  }
}
