package com.genial.demo.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorageResponse {
        private UserResponse user;
        private String id;
        private String name;
        private String description;
        private List<ProductResponse> products;

        public StorageResponse(String name, String description, List<ProductResponse> products, UserResponse user) {
                this.name = name;
                this.description = description;
                this.products = products;
                this.user = user;
        }
}
