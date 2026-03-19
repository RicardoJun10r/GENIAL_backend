package com.genial.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genial.demo.config.SecurityFilter;
import com.genial.demo.exceptions.GlobalExceptionHandler;
import com.genial.demo.modules.app.controller.StorageController;
import com.genial.demo.modules.app.services.StorageService;
import com.genial.demo.shared.StorageCreate;
import com.genial.demo.shared.StorageResponse;

@WebMvcTest(StorageController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class StorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StorageService storageService;

    @MockitoBean
    private SecurityFilter securityFilter;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @SuppressWarnings("null")
    @Test
    void should_Return201_When_AddStorage() throws Exception {
        // Given
        final StorageResponse response = new StorageResponse();
        response.setId("storage-1");
        response.setName("Main Storage");
        given(storageService.addStorageOnUser(any(StorageCreate.class))).willReturn(response);

        final StorageCreate request = new StorageCreate("Main Storage", "Central warehouse");

        // When + Then
        mockMvc.perform(post("/api/v1/storages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("storage-1"))
                .andExpect(jsonPath("$.name").value("Main Storage"));
    }

    @Test
    void should_Return200_When_GetByName() throws Exception {
        // Given
        final StorageResponse response = new StorageResponse();
        response.setId("storage-1");
        response.setName("Main Storage");
        given(storageService.findByName("Main Storage")).willReturn(response);

        // When + Then
        mockMvc.perform(get("/api/v1/storages").param("name", "Main Storage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("storage-1"))
                .andExpect(jsonPath("$.name").value("Main Storage"));
    }

    @SuppressWarnings("null")
    @Test
    void should_Return400_When_UpdateStorageWithoutRequiredFields() throws Exception {
        // Given
        final String invalidPayload = "";

        // When + Then
        mockMvc.perform(put("/api/v1/storages/{id}", "storage-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_Return204_When_DeleteStorage() throws Exception {
        // Given
        // When + Then
        mockMvc.perform(delete("/api/v1/storages/{id}", "storage-1"))
                .andExpect(status().isNoContent());

        then(storageService).should().delete("storage-1");
    }
}
