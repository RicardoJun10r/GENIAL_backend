package com.genial.demo.modules.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.genial.demo.modules.app.services.StorageService;
import com.genial.demo.shared.StorageCreate;
import com.genial.demo.shared.StorageResponse;
import com.genial.demo.shared.StorageUpdate;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/storages")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @PostMapping
    public ResponseEntity<StorageResponse> addStorageOnUser(
            @Valid @RequestBody StorageCreate storage) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.storageService.addStorageOnUser(storage));
    }

    @GetMapping
    public ResponseEntity<StorageResponse> getByName(@RequestParam("name") String name) {
        return ResponseEntity.ok().body(this.storageService.findByName(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StorageResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok().body(this.storageService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        this.storageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<StorageResponse> update(@PathVariable String id,
            @Valid @RequestBody StorageUpdate dto) {
        final StorageUpdate updateRequest = withPathId(id, dto);
        return ResponseEntity.ok().body(this.storageService.update(updateRequest));
    }

    private StorageUpdate withPathId(final String id, final StorageUpdate dto) {
        return new StorageUpdate(id, dto.name(), dto.description());
    }
}
