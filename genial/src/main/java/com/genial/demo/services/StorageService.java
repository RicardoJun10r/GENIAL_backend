package com.genial.demo.services;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genial.demo.DTO.StorageCreate;
import com.genial.demo.DTO.StorageResponse;
import com.genial.demo.DTO.StorageUpdate;
import com.genial.demo.DTO.UserResponse;
import com.genial.demo.entity.Storage;
import com.genial.demo.entity.User;
import com.genial.demo.repositories.StorageRepository;
import com.genial.demo.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final StorageRepository storageRepository;

    private final UserRepository userRepository;

    private final ModelMapper mapper;

    @Transactional
    public StorageResponse addStorageOnUser(String id_user, StorageCreate storage) {
        Optional<User> user = this.userRepository.findById(id_user);
        if (user.isPresent()) {
            Storage novo_storage = new Storage(storage.name(), storage.description());
            novo_storage.setUser(user.get());
            user.get().getStorages().add(
                    this.storageRepository.save(novo_storage));
            this.userRepository.save(user.get());
            return new StorageResponse(
                    novo_storage.getName(),
                    novo_storage.getDescription(), novo_storage.getProducts(),
                    mapper.map(novo_storage.getUser(), UserResponse.class));
        }
        throw new RuntimeException("Erro");
    }

    public StorageResponse findByName(String email, String name) {
        Optional<User> user = this.userRepository.findByEmail(email);
        if (user.isPresent()) {
            return this.mapper.map(
                    user.get().getStorages().stream().filter(s -> s.getName().equals(name)).findFirst().get(),
                    StorageResponse.class);
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
            return this.mapper.map(this.storageRepository.save(storage.get()), StorageResponse.class);
        }
        throw new RuntimeException("Erro");
    }

}
