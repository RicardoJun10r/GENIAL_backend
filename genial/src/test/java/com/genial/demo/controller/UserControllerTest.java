package com.genial.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

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
import com.genial.demo.exceptions.BusinessException;
import com.genial.demo.exceptions.GlobalExceptionHandler;
import com.genial.demo.modules.app.controller.UserController;
import com.genial.demo.modules.app.services.UserService;
import com.genial.demo.shared.CreateUserRequest;
import com.genial.demo.shared.UserDto;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private SecurityFilter securityFilter;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void should_Return200_When_GetById() throws Exception {
        // Given
        final UserDto response = new UserDto("user-1", "user@genial.com", "User", Collections.emptyList());
        given(userService.findByUuid("user-1")).willReturn(response);

        // When + Then
        mockMvc.perform(get("/api/v1/users/{id}", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user-1"))
                .andExpect(jsonPath("$.email").value("user@genial.com"))
                .andExpect(jsonPath("$.name").value("User"));
    }

    @SuppressWarnings("null")
    @Test
    void should_Return201_When_SaveUser() throws Exception {
        // Given
        final UserDto response = new UserDto("user-1", "user@genial.com", "User", Collections.emptyList());
        given(userService.saveUser(any(CreateUserRequest.class))).willReturn(response);

        final CreateUserRequest request = new CreateUserRequest("user@genial.com", "User", "plain");

        // When + Then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("user-1"))
                .andExpect(jsonPath("$.email").value("user@genial.com"));
    }

    @SuppressWarnings("null")
    @Test
    void should_Return400_When_SaveUserWithInvalidPayload() throws Exception {
        // Given
        given(userService.saveUser(any(CreateUserRequest.class)))
                .willThrow(new BusinessException("Erro ao cadastrar usuraio: Email deve ser informado."));

        final String invalidPayload = "{\"email\":null,\"name\":null,\"password\":null}";

        // When + Then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_Return204_When_DeleteUser() throws Exception {
        // Given
        // When + Then
        mockMvc.perform(delete("/api/v1/users/{id}", "user-1"))
                .andExpect(status().isNoContent());

        then(userService).should().deleteById("user-1");
    }
}
