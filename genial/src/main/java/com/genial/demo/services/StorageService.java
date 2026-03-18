package com.genial.demo.services;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genial.demo.model.Storage;
import com.genial.demo.model.User;
import com.genial.demo.repositories.StorageRepository;
import com.genial.demo.repositories.UserRepository;
import com.genial.demo.shared.StorageCreate;
import com.genial.demo.shared.StorageResponse;
import com.genial.demo.shared.StorageUpdate;
import com.genial.demo.shared.mapper.StorageMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final StorageRepository storageRepository;

    private final UserRepository userRepository;

    private final StorageMapper mapper;

    @Transactional
    public StorageResponse addStorageOnUser(String id_user, StorageCreate storage) {
        Optional<User> user = this.userRepository.findById(id_user);
        if (user.isPresent()) {
            Storage novo_storage = mapper.toEntity(storage);
            novo_storage.setUser(user.get());
            Storage savedStorage = this.storageRepository.save(novo_storage);
            user.get().getStorages().add(savedStorage);
            this.userRepository.save(user.get());
            return mapper.toResponse(savedStorage);
        }
        throw new RuntimeException("Erro");
    }

    public StorageResponse findByName(String email, String name) {
        Optional<User> user = this.userRepository.findByEmail(email);
        if (user.isPresent()) {
            return this.mapper.toResponse(
                    user.get().getStorages().stream().filter(s -> s.getName().equals(name)).findFirst().get());
        }
        throw new RuntimeException("Erro");
    }

    public StorageResponse findById(String id) {
        Optional<Storage> storage = this.storageRepository.findById(id);
        if (storage.isPresent()) {
            return this.mapper.toResponse(storage.get());
        }
        throw new RuntimeException("Erro");
    }

    public void delete(String id) {
        this.storageRepository.deleteById(id);
    }

    @Transactional
    public StorageResponse update(StorageUpdate dto) {
        Optional<Storage> storage = storageRepository.findById(dto.id());
        if (storage.isPresent()) {
            if (!dto.name().isEmpty() && !dto.name().isBlank()) {
                storage.get().setName(dto.name());
            }
            if (!dto.description().isEmpty() && !dto.description().isBlank()) {
                storage.get().setDescription(dto.description());
            }
            return this.mapper.toResponse(this.storageRepository.save(storage.get()));
        }
        throw new RuntimeException("Erro");
    }

}
