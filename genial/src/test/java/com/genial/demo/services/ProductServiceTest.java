package com.genial.demo.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.genial.demo.exceptions.BusinessException;
import com.genial.demo.exceptions.ResourceNotFoundException;
import com.genial.demo.exceptions.UnauthorizedAccessException;
import com.genial.demo.modules.app.model.Product;
import com.genial.demo.modules.app.model.Storage;
import com.genial.demo.modules.app.model.User;
import com.genial.demo.modules.app.repositories.ProductRepository;
import com.genial.demo.modules.app.repositories.StorageRepository;
import com.genial.demo.modules.app.services.ProductService;
import com.genial.demo.shared.ProductCreate;
import com.genial.demo.shared.ProductResponse;
import com.genial.demo.shared.ProductUpdate;
import com.genial.demo.shared.mapper.ProductMapper;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StorageRepository storageRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @SuppressWarnings("null")
    @Test
    void should_AddProduct_When_StorageBelongsToUser() {
        // Given
        mockAuthenticatedUser("user-1");
        final ProductCreate request = new ProductCreate("Notebook", "Work notebook", "TECH", new BigDecimal("1000.00"),
                2);

        final Storage storage = new Storage();
        storage.setId("storage-1");

        final Product mappedProduct = new Product();
        mappedProduct.setName("Notebook");

        final Product savedProduct = new Product();
        savedProduct.setId("product-1");
        savedProduct.setName("Notebook");
        savedProduct.setStorage(storage);

        final ProductResponse expectedResponse = new ProductResponse();
        expectedResponse.setId("product-1");
        expectedResponse.setName("Notebook");

        given(storageRepository.findByIdAndUserId("storage-1", "user-1"))
                .willReturn(Optional.of(storage));
        given(productMapper.toEntity(request)).willReturn(mappedProduct);
        given(productRepository.save(mappedProduct)).willReturn(savedProduct);
        given(productMapper.toResponse(savedProduct)).willReturn(expectedResponse);

        // When
        final ProductResponse response = productService.addProductOnStorage("storage-1", request);

        // Then
        assertThat(response.getId()).isEqualTo("product-1");
        assertThat(response.getName()).isEqualTo("Notebook");

        final ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        then(productRepository).should().save(captor.capture());
        assertThat(captor.getValue().getStorage().getId()).isEqualTo("storage-1");
        then(storageRepository).should().findByIdAndUserId("storage-1", "user-1");
    }

    @SuppressWarnings("null")
    @Test
    void should_UpdateProduct_When_DataIsValid() {
        // Given
        mockAuthenticatedUser("user-1");
        final ProductUpdate request = new ProductUpdate(
                "product-1",
                "Updated Notebook",
                "Updated description",
                "OFFICE",
                new BigDecimal("1500.00"),
                10);

        final Product existingProduct = new Product();
        existingProduct.setId("product-1");
        existingProduct.setName("Old Notebook");
        existingProduct.setDescription("Old description");
        existingProduct.setSector("TECH");
        existingProduct.setValue(new BigDecimal("1000.00"));
        existingProduct.setQuantidade(2);

        given(productRepository.findByIdAndStorageUserId("product-1", "user-1"))
                .willReturn(Optional.of(existingProduct));
        given(productRepository.save(any(Product.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(productMapper.toResponse(any(Product.class))).willAnswer(invocation -> {
            final Product product = invocation.getArgument(0);
            final ProductResponse response = new ProductResponse();
            response.setId(product.getId());
            response.setName(product.getName());
            response.setDescription(product.getDescription());
            response.setSector(product.getSector());
            response.setValue(product.getValue());
            response.setQuantidade(product.getQuantidade());
            return response;
        });

        // When
        final ProductResponse response = productService.update(request);

        // Then
        assertThat(response.getId()).isEqualTo("product-1");
        assertThat(response.getName()).isEqualTo("Updated Notebook");
        assertThat(response.getDescription()).isEqualTo("Updated description");
        assertThat(response.getSector()).isEqualTo("OFFICE");
        assertThat(response.getValue()).isEqualByComparingTo("1500.00");
        assertThat(response.getQuantidade()).isEqualTo(10);

        final ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        then(productRepository).should().save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Updated Notebook");
        assertThat(captor.getValue().getDescription()).isEqualTo("Updated description");
        assertThat(captor.getValue().getSector()).isEqualTo("OFFICE");
        assertThat(captor.getValue().getValue()).isEqualByComparingTo("1500.00");
        assertThat(captor.getValue().getQuantidade()).isEqualTo(10);
    }

    @Test
    void should_ThrowResourceNotFoundException_When_DeletingOthersProduct() {
        // Given
        mockAuthenticatedUser("user-1");
        given(productRepository.findByIdAndStorageUserId("product-1", "user-1"))
                .willReturn(Optional.empty());

        // When + Then
        assertThatThrownBy(() -> productService.delete("product-1"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void should_ThrowBusinessException_When_UpdatingWithNullPayload() {
        // Given
        mockAuthenticatedUser("user-1");

        // When + Then
        assertThatThrownBy(() -> productService.update(null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Dados de atualizacao de produto nao informados.");
    }

    @Test
    void should_ThrowUnauthorizedAccessException_When_UserIsNotAuthenticated() {
        // Given + When + Then
        assertThatThrownBy(() -> productService.findById("product-1"))
                .isInstanceOf(UnauthorizedAccessException.class)
                .hasMessage("Usuario autenticado nao informado.");
    }

    private void mockAuthenticatedUser(final String userId) {
        final User principal = new User();
        principal.setId(userId);

        final SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(
                principal,
                null,
                Collections.emptyList()));
        SecurityContextHolder.setContext(context);
    }
}
