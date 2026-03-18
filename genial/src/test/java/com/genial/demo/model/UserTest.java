package com.genial.demo.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  void should_InitializeStorages_When_UserIsPersistedWithNullStorages() {
    // Given
    User user = new User("john@genial.com", "John", "hashed-password");
    user.setStorages(null);

    // When
    user.onCreate();

    // Then
    assertThat(user.getStorages()).isNotNull();
    assertThat(user.getStorages()).isEmpty();
  }
}
