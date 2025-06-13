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

import com.genial.demo.DTO.StorageCreate;
import com.genial.demo.DTO.StorageResponse;
import com.genial.demo.DTO.StorageUpdate;
import com.genial.demo.services.StorageService;

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
