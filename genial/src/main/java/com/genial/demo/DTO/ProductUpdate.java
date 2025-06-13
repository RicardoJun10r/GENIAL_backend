package com.genial.demo.DTO;

import java.time.LocalDate;

public record ProductUpdate(
        String id,
        String name,
        String description,
        String sector,
        Double value,
        LocalDate date,
        Integer quantidade) {
}
