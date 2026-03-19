package com.genial.demo.modules.app.services;

import java.util.function.Consumer;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genial.demo.exceptions.BusinessException;
import com.genial.demo.exceptions.ResourceNotFoundException;
import com.genial.demo.exceptions.UnauthorizedAccessException;
import com.genial.demo.modules.app.model.Storage;
import com.genial.demo.modules.app.model.User;
import com.genial.demo.modules.app.repositories.StorageRepository;
import com.genial.demo.modules.app.repositories.UserRepository;
import com.genial.demo.shared.StorageCreate;
import com.genial.demo.shared.StorageResponse;
import com.genial.demo.shared.StorageUpdate;
import com.genial.demo.shared.mapper.StorageMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageService {

    private final StorageRepository storageRepository;
    private final UserRepository userRepository;
    private final StorageMapper mapper;

    @Transactional
    public StorageResponse addStorageOnUser(final StorageCreate storageRequest) {
        final String userId = getCurrentUserId();
        if (storageRequest == null) {
            throw new BusinessException("Dados do storage nao informados.");
        }

        @SuppressWarnings("null")
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado para o id informado."));

        final Storage newStorage = mapper.toEntity(storageRequest);
        newStorage.setUser(user);

        final Storage savedStorage = storageRepository.save(newStorage);
        return mapper.toResponse(savedStorage);
    }

    @Transactional(readOnly = true)
    public StorageResponse findByName(final String name) {
        final String userId = getCurrentUserId();
        validateRequired(name, "Nome deve ser informado para buscar storage.");

        final Storage storage = storageRepository.findByNameAndUserId(name, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Storage nao encontrado para o nome e usuario informados."));
        return mapper.toResponse(storage);
    }

    @Transactional(readOnly = true)
    public StorageResponse findById(final String id) {
        final String userId = getCurrentUserId();
        validateRequired(id, "Id deve ser informado para buscar storage.");

        final Storage storage = storageRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Storage nao encontrado para o usuario informado."));
        return mapper.toResponse(storage);
    }

    @SuppressWarnings("null")
    @Transactional
    public void delete(final String id) {
        final String userId = getCurrentUserId();
        validateRequired(id, "Id deve ser informado para deletar storage.");

        final Storage storage = storageRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> {
                    log.warn(
                            "Falha na operacao [DELETE] para o Usuario [{}]. Recurso: [STORAGE] [{}]. Motivo: recurso nao encontrado ou sem permissao.",
                            userId, id);
                    return new ResourceNotFoundException(
                            "Nao e possivel deletar: Storage nao encontrado para o usuario informado.");
                });

        storageRepository.delete(storage);
    }

    @Transactional
    public StorageResponse update(final StorageUpdate dto) {
        final String userId = getCurrentUserId();
        if (dto == null) {
            throw new BusinessException("Dados de atualizacao de storage nao informados.");
        }

        validateRequired(dto.id(), "Id do storage deve ser informado para atualizacao.");

        final Storage storage = storageRepository.findByIdAndUserId(dto.id(), userId)
                .orElseThrow(() -> {
                    log.warn(
                            "Falha na operacao [UPDATE] para o Usuario [{}]. Recurso: [STORAGE] [{}]. Motivo: recurso nao encontrado ou sem permissao.",
                            userId, dto.id());
                    return new ResourceNotFoundException(
                            "Nao e possivel atualizar:Storage nao encontrado para o usuario informado.");
                });

        updateTextField(dto.name(), storage::setName);
        updateTextField(dto.description(), storage::setDescription);

        return mapper.toResponse(storageRepository.save(storage));
    }

    private void validateRequired(final String value, final String message) {
        if (!hasText(value)) {
            throw new BusinessException(message);
        }
    }

    private void updateTextField(final String value, final Consumer<String> updateAction) {
        if (hasText(value)) {
            updateAction.accept(value.trim());
        }
    }

    private boolean hasText(final String value) {
        return value != null && !value.isBlank();
    }

    private String getCurrentUserId() {
        final Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new UnauthorizedAccessException("Usuario autenticado nao informado.");
        }

        if (!hasText(user.getId())) {
            throw new UnauthorizedAccessException("Usuario autenticado nao informado.");
        }
        return user.getId();
    }
}
