package com.genial.demo.modules.app.services;

import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genial.demo.exceptions.BusinessException;
import com.genial.demo.exceptions.ResourceNotFoundException;
import com.genial.demo.exceptions.UnauthorizedAccessException;
import com.genial.demo.modules.app.model.Product;
import com.genial.demo.modules.app.model.Storage;
import com.genial.demo.modules.app.model.User;
import com.genial.demo.modules.app.repositories.ProductRepository;
import com.genial.demo.modules.app.repositories.StorageRepository;
import com.genial.demo.shared.ProductCreate;
import com.genial.demo.shared.ProductResponse;
import com.genial.demo.shared.ProductUpdate;
import com.genial.demo.shared.mapper.ProductMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StorageRepository storageRepository;
    private final ProductMapper mapper;

    @Transactional
    public ProductResponse addProductOnStorage(final String storageId, final ProductCreate productRequest) {
        final String userId = getCurrentUserId();
        validateRequired(storageId, "Id do storage deve ser informado para cadastrar produto.");
        if (productRequest == null) {
            throw new BusinessException("Dados do produto nao informados.");
        }

        final Storage storage = storageRepository.findByIdAndUserId(storageId, userId)
                .orElseThrow(() -> {
                    log.warn(
                            "Falha na operacao [CREATE] para o Usuario [{}]. Recurso: [STORAGE] [{}]. Motivo: recurso nao encontrado ou sem permissao.",
                            userId, storageId);
                    return new ResourceNotFoundException(
                            "Storage nao encontrado para o usuario informado.");
                });

        final Product newProduct = mapper.toEntity(productRequest);
        newProduct.setStorage(storage);

        final Product savedProduct = productRepository.save(newProduct);
        return mapper.toResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(final String id) {
        final String userId = getCurrentUserId();
        validateRequired(id, "Id deve ser informado para buscar produto.");

        final Product product = productRepository.findByIdAndStorageUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produto nao encontrado para o usuario informado."));
        return mapper.toResponse(product);
    }

    @SuppressWarnings("null")
    @Transactional
    public void delete(final String id) {
        final String userId = getCurrentUserId();
        validateRequired(id, "Id deve ser informado para deletar produto.");

        final Product product = productRepository.findByIdAndStorageUserId(id, userId)
                .orElseThrow(() -> {
                    log.warn(
                            "Falha na operacao [DELETE] para o Usuario [{}]. Recurso: [PRODUCT] [{}]. Motivo: recurso nao encontrado ou sem permissao.",
                            userId, id);
                    return new ResourceNotFoundException(
                            "Nao e possivel deletar: Produto nao encontrado para o usuario informado.");
                });

        productRepository.delete(product);
    }

    @Transactional
    public ProductResponse update(final ProductUpdate dto) {
        final String userId = getCurrentUserId();
        if (dto == null) {
            throw new BusinessException("Dados de atualizacao de produto nao informados.");
        }

        validateRequired(dto.id(), "Id do produto deve ser informado para atualizacao.");

        final Product productToUpdate = productRepository.findByIdAndStorageUserId(dto.id(), userId)
                .orElseThrow(() -> {
                    log.warn(
                            "Falha na operacao [UPDATE] para o Usuario [{}]. Recurso: [PRODUCT] [{}]. Motivo: recurso nao encontrado ou sem permissao.",
                            userId, dto.id());
                    return new ResourceNotFoundException(
                            "Nao e possivel atualizar: Produto nao encontrado para o usuario informado.");
                });

        applyUpdates(dto, productToUpdate);

        @SuppressWarnings("null")
        final Product updatedProduct = productRepository.save(productToUpdate);
        return mapper.toResponse(updatedProduct);
    }

    private void applyUpdates(final ProductUpdate dto, final Product productToUpdate) {
        updateTextField(dto.name(), productToUpdate::setName);
        updateTextField(dto.description(), productToUpdate::setDescription);
        updateTextField(dto.sector(), productToUpdate::setSector);

        Optional.ofNullable(dto.value()).ifPresent(productToUpdate::setValue);
        Optional.ofNullable(dto.quantidade()).ifPresent(productToUpdate::setQuantidade);
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
