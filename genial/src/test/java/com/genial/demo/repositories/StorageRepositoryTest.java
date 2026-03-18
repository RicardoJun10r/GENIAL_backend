package com.genial.demo.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import com.genial.demo.model.Storage;
import com.genial.demo.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:storagerepo;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false;NON_KEYWORDS=VALUE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class StorageRepositoryTest {

  @Autowired
  private StorageRepository storageRepository;

  @Autowired
  private UserRepository userRepository;

  @Test
  void should_ReturnStorage_When_IdBelongsToUser() {
    // Given
    User owner = userRepository.saveAndFlush(new User("storage-owner@genial.com", "Owner", "pass"));
    Storage storage = new Storage("Main Warehouse", "Central");
    storage.setUser(owner);
    Storage savedStorage = storageRepository.saveAndFlush(storage);

    // When
    var found = storageRepository.findByIdAndUserId(savedStorage.getId(), owner.getId());

    // Then
    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(savedStorage.getId());
  }

  @Test
  void should_ReturnEmpty_When_StorageDoesNotBelongToUser() {
    // Given
    User owner = userRepository.saveAndFlush(new User("storage-owner2@genial.com", "Owner", "pass"));
    User anotherUser = userRepository.saveAndFlush(new User("storage-other@genial.com", "Other", "pass"));
    Storage storage = new Storage("Main Warehouse", "Central");
    storage.setUser(owner);
    Storage savedStorage = storageRepository.saveAndFlush(storage);

    // When
    var found = storageRepository.findByIdAndUserId(savedStorage.getId(), anotherUser.getId());

    // Then
    assertThat(found).isEmpty();
  }

  @SuppressWarnings("null")
  @Test
  void should_ReturnOnlyUserStoragesWithPagination_When_FindingByUserId() {
    // Given
    User owner = userRepository.saveAndFlush(new User("storage-owner3@genial.com", "Owner", "pass"));
    User anotherUser = userRepository.saveAndFlush(new User("storage-other3@genial.com", "Other", "pass"));

    Storage ownerStorageA = new Storage("Warehouse A", "A");
    ownerStorageA.setUser(owner);
    Storage ownerStorageB = new Storage("Warehouse B", "B");
    ownerStorageB.setUser(owner);
    Storage otherStorage = new Storage("Warehouse C", "C");
    otherStorage.setUser(anotherUser);

    storageRepository.saveAll(List.of(ownerStorageA, ownerStorageB, otherStorage));
    storageRepository.flush();

    // When
    Page<Storage> page = storageRepository.findByUserId(owner.getId(), PageRequest.of(0, 10));

    // Then
    assertThat(page.getTotalElements()).isEqualTo(2);
    assertThat(page.getContent())
        .extracting(Storage::getName)
        .containsExactlyInAnyOrder("Warehouse A", "Warehouse B");
  }

  @SuppressWarnings("null")
  @Test
  void should_IgnoreInactiveStorages_When_FindingByUserId() {
    // Given
    User owner = userRepository.saveAndFlush(new User("storage-owner4@genial.com", "Owner", "pass"));

    Storage activeStorage = new Storage("Warehouse Active", "A");
    activeStorage.setUser(owner);
    Storage inactiveStorage = new Storage("Warehouse Inactive", "I");
    inactiveStorage.setUser(owner);
    inactiveStorage.setActive(false);

    storageRepository.saveAll(List.of(activeStorage, inactiveStorage));
    storageRepository.flush();

    // When
    Page<Storage> page = storageRepository.findByUserId(owner.getId(), PageRequest.of(0, 10));

    // Then
    assertThat(page.getTotalElements()).isEqualTo(1);
    assertThat(page.getContent()).extracting(Storage::getName).containsExactly("Warehouse Active");
  }
}
