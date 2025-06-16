package com.genial.demo.DTO;

import java.time.LocalDate;

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

        private Double value;

        private LocalDate date;

        private Integer quantidade;
}
