package com.genial.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.argThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

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
import com.genial.demo.modules.app.controller.ProductController;
import com.genial.demo.modules.app.services.ProductService;
import com.genial.demo.shared.ProductCreate;
import com.genial.demo.shared.ProductResponse;
import com.genial.demo.shared.ProductUpdate;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private ProductService productService;

        @MockitoBean
        private SecurityFilter securityFilter;

        @MockitoBean
        private JpaMetamodelMappingContext jpaMetamodelMappingContext;

        @SuppressWarnings("null")
        @Test
        void should_Return201_When_AddProductOnStorage() throws Exception {
                // Given
                final ProductResponse response = new ProductResponse();
                response.setId("product-1");
                response.setName("Notebook");

                given(productService.addProductOnStorage(any(String.class), any(ProductCreate.class)))
                                .willReturn(response);

                final ProductCreate request = new ProductCreate("Notebook", "Work notebook", "TECH",
                                new BigDecimal("1000.00"), 2);

                // When + Then
                mockMvc.perform(post("/api/v1/products/storages/{storageId}", "storage-1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value("product-1"))
                                .andExpect(jsonPath("$.name").value("Notebook"));
        }

        @Test
        void should_Return200_When_GetById() throws Exception {
                // Given
                final ProductResponse response = new ProductResponse();
                response.setId("product-1");
                response.setName("Notebook");
                given(productService.findById("product-1")).willReturn(response);

                // When + Then
                mockMvc.perform(get("/api/v1/products/{id}", "product-1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value("product-1"))
                                .andExpect(jsonPath("$.name").value("Notebook"));
        }

        @SuppressWarnings("null")
        @Test
        void should_Return200_When_UpdateProduct() throws Exception {
                // Given
                final ProductResponse response = new ProductResponse();
                response.setId("product-1");
                response.setName("Updated Notebook");
                given(productService.update(any(ProductUpdate.class))).willReturn(response);

                final ProductUpdate request = new ProductUpdate(null, "Updated Notebook", "Updated", "OFFICE",
                                new BigDecimal("1500.00"), 3);

                // When + Then
                mockMvc.perform(put("/api/v1/products/{id}", "product-1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value("product-1"))
                                .andExpect(jsonPath("$.name").value("Updated Notebook"));

                then(productService).should().update(argThat(update -> "product-1".equals(update.id())));
        }

        @Test
        void should_Return204_When_DeleteProduct() throws Exception {
                // Given
                // When + Then
                mockMvc.perform(delete("/api/v1/products/{id}", "product-1"))
                                .andExpect(status().isNoContent());

                then(productService).should().delete("product-1");
        }
}
