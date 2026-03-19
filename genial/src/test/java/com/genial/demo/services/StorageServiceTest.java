package com.genial.demo.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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
import com.genial.demo.modules.app.model.Storage;
import com.genial.demo.modules.app.model.User;
import com.genial.demo.modules.app.repositories.StorageRepository;
import com.genial.demo.modules.app.repositories.UserRepository;
import com.genial.demo.modules.app.services.StorageService;
import com.genial.demo.shared.StorageCreate;
import com.genial.demo.shared.StorageResponse;
import com.genial.demo.shared.mapper.StorageMapper;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    private StorageRepository storageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StorageMapper storageMapper;

    @InjectMocks
    private StorageService storageService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @SuppressWarnings("null")
    @Test
    void should_AddStorage_When_UserExists() {
        // Given
        mockAuthenticatedUser("user-1");
        final StorageCreate request = new StorageCreate("Main Storage", "Central warehouse");
        final User user = new User("user@genial.com", "User", "encoded");
        user.setId("user-1");

        final Storage mappedStorage = new Storage();
        mappedStorage.setName("Main Storage");

        final Storage savedStorage = new Storage();
        savedStorage.setId("storage-1");
        savedStorage.setName("Main Storage");
        savedStorage.setDescription("Central warehouse");
        savedStorage.setUser(user);

        final StorageResponse expectedResponse = new StorageResponse();
        expectedResponse.setId("storage-1");
        expectedResponse.setName("Main Storage");

        given(userRepository.findById("user-1")).willReturn(Optional.of(user));
        given(storageMapper.toEntity(request)).willReturn(mappedStorage);
        given(storageRepository.save(mappedStorage)).willReturn(savedStorage);
        given(storageMapper.toResponse(savedStorage)).willReturn(expectedResponse);

        // When
        final StorageResponse response = storageService.addStorageOnUser(request);

        // Then
        assertThat(response.getId()).isEqualTo("storage-1");
        assertThat(response.getName()).isEqualTo("Main Storage");

        final ArgumentCaptor<Storage> captor = ArgumentCaptor.forClass(Storage.class);
        then(storageRepository).should().save(captor.capture());
        assertThat(captor.getValue().getUser().getId()).isEqualTo("user-1");
    }

    @Test
    void should_ReturnStorage_When_FindByIdAndUserIdMatches() {
        // Given
        mockAuthenticatedUser("user-1");
        final Storage storage = new Storage();
        storage.setId("storage-1");

        final StorageResponse expectedResponse = new StorageResponse();
        expectedResponse.setId("storage-1");
        expectedResponse.setName("Main Storage");

        given(storageRepository.findByIdAndUserId("storage-1", "user-1"))
                .willReturn(Optional.of(storage));
        given(storageMapper.toResponse(storage)).willReturn(expectedResponse);

        // When
        final StorageResponse response = storageService.findById("storage-1");

        // Then
        assertThat(response.getId()).isEqualTo("storage-1");
        assertThat(response.getName()).isEqualTo("Main Storage");
    }

    @Test
    void should_ThrowResourceNotFoundException_When_StorageDoesNotBelongToUser() {
        // Given
        mockAuthenticatedUser("user-1");
        given(storageRepository.findByIdAndUserId("storage-1", "user-1"))
                .willReturn(Optional.empty());

        // When + Then
        assertThatThrownBy(() -> storageService.findById("storage-1"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void should_DeleteStorage_When_UserOwnsStorage() {
        // Given
        mockAuthenticatedUser("user-1");
        final Storage storage = new Storage();
        storage.setId("storage-1");

        given(storageRepository.findByIdAndUserId("storage-1", "user-1"))
                .willReturn(Optional.of(storage));

        // When
        storageService.delete("storage-1");

        // Then
        then(storageRepository).should().delete(storage);
    }

    @Test
    void should_ThrowBusinessException_When_DeletingStorageWithoutId() {
        // Given
        mockAuthenticatedUser("user-1");

        // When + Then
        assertThatThrownBy(() -> storageService.delete(" "))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void should_ThrowUnauthorizedAccessException_When_UserIsNotAuthenticated() {
        // Given + When + Then
        assertThatThrownBy(() -> storageService.findById("storage-1"))
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
