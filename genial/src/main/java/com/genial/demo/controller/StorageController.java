package com.genial.demo.controller;

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

import com.genial.demo.services.StorageService;
import com.genial.demo.shared.StorageCreate;
import com.genial.demo.shared.StorageResponse;
import com.genial.demo.shared.StorageUpdate;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/estoque")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @PostMapping("/{user}")
    public ResponseEntity<StorageResponse> addStorageOnUser(@PathVariable String user,
            @RequestBody StorageCreate storage) {
        return ResponseEntity.ok().body(this.storageService.addStorageOnUser(user, storage));
    }

    @GetMapping("/{email}/buscar")
    public ResponseEntity<StorageResponse> getByName(@PathVariable String email, @RequestParam("name") String name) {
        return ResponseEntity.ok().body(this.storageService.findByName(email, name));
    }

    @GetMapping("/buscar")
    public ResponseEntity<StorageResponse> getById(@RequestParam("id") String id) {
        return ResponseEntity.ok().body(this.storageService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        this.storageService.delete(id);
        return ResponseEntity.ok().body("Deletado");
    }

    @PutMapping("/{name}")
    public ResponseEntity<StorageResponse> update(@RequestBody StorageUpdate dto) {
        return ResponseEntity.ok().body(this.storageService.update(dto));
    }

}
