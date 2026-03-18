package com.genial.demo.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import com.genial.demo.model.Product;
import com.genial.demo.model.Storage;
import com.genial.demo.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:productrepo;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false;NON_KEYWORDS=VALUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void should_ReturnProduct_When_IdBelongsToUserStorage() {
        // Given
        User owner = userRepository.saveAndFlush(new User("owner@genial.com", "Owner", "pass"));
        Storage storage = new Storage("Owner Storage", "Main");
        storage.setUser(owner);
        Storage savedStorage = storageRepository.saveAndFlush(storage);

        Product product = new Product("Notebook", "Ultrabook", "TECH", new BigDecimal("100.0000"), 2);
        product.setStorage(savedStorage);
        Product savedProduct = productRepository.saveAndFlush(product);

        // When
        var found = productRepository.findByIdAndStorageUserId(savedProduct.getId(), owner.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(savedProduct.getId());
    }

    @Test
    void should_ReturnEmpty_When_ProductDoesNotBelongToUserStorage() {
        // Given
        User owner = userRepository.saveAndFlush(new User("owner2@genial.com", "Owner", "pass"));
        User anotherUser = userRepository.saveAndFlush(new User("other@genial.com", "Other", "pass"));
        Storage storage = new Storage("Owner Storage", "Main");
        storage.setUser(owner);
        Storage savedStorage = storageRepository.saveAndFlush(storage);

        Product product = new Product("Mouse", "Wireless", "TECH", new BigDecimal("50.0000"), 3);
        product.setStorage(savedStorage);
        Product savedProduct = productRepository.saveAndFlush(product);

        // When
        var found = productRepository.findByIdAndStorageUserId(savedProduct.getId(), anotherUser.getId());

        // Then
        assertThat(found).isEmpty();
    }

    @SuppressWarnings("null")
    @Test
    void should_ReturnOnlyUserProductsWithPagination_When_FindingByStorageUserId() {
        // Given
        User owner = userRepository.saveAndFlush(new User("owner3@genial.com", "Owner", "pass"));
        User anotherUser = userRepository.saveAndFlush(new User("other3@genial.com", "Other", "pass"));

        Storage ownerStorage = new Storage("Owner Storage", "Main");
        ownerStorage.setUser(owner);
        Storage savedOwnerStorage = storageRepository.saveAndFlush(ownerStorage);

        Storage otherStorage = new Storage("Other Storage", "Secondary");
        otherStorage.setUser(anotherUser);
        Storage savedOtherStorage = storageRepository.saveAndFlush(otherStorage);

        Product firstOwnerProduct = new Product("Product A", "Desc A", "TECH", new BigDecimal("10.0000"), 1);
        firstOwnerProduct.setStorage(savedOwnerStorage);
        Product secondOwnerProduct = new Product("Product B", "Desc B", "TECH", new BigDecimal("20.0000"), 2);
        secondOwnerProduct.setStorage(savedOwnerStorage);
        Product otherProduct = new Product("Product C", "Desc C", "TECH", new BigDecimal("30.0000"), 3);
        otherProduct.setStorage(savedOtherStorage);

        productRepository.saveAll(List.of(firstOwnerProduct, secondOwnerProduct, otherProduct));
        productRepository.flush();

        // When
        Page<Product> page = productRepository.findAllByStorageUserId(owner.getId(), PageRequest.of(0, 10));

        // Then
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent())
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Product A", "Product B");
    }

    @SuppressWarnings("null")
    @Test
    void should_IgnoreInactiveProducts_When_FindingByStorageUserId() {
        // Given
        User owner = userRepository.saveAndFlush(new User("owner4@genial.com", "Owner", "pass"));
        Storage storage = new Storage("Owner Storage", "Main");
        storage.setUser(owner);
        Storage savedStorage = storageRepository.saveAndFlush(storage);

        Product activeProduct = new Product("Active Product", "Desc", "TECH", new BigDecimal("10.0000"), 1);
        activeProduct.setStorage(savedStorage);

        Product inactiveProduct = new Product("Inactive Product", "Desc", "TECH", new BigDecimal("11.0000"), 1);
        inactiveProduct.setStorage(savedStorage);
        inactiveProduct.setActive(false);

        productRepository.saveAll(List.of(activeProduct, inactiveProduct));
        productRepository.flush();

        // When
        Page<Product> page = productRepository.findAllByStorageUserId(owner.getId(), PageRequest.of(0, 10));

        // Then
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent()).extracting(Product::getName).containsExactly("Active Product");
    }
}
