package com.genial.demo.shared;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

        private String id;

        private String name;

        private String description;

        private String sector;

        private BigDecimal value;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;

        private Integer quantidade;
}
