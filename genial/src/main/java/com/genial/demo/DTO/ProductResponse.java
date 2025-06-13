package com.genial.demo.DTO;

import java.time.LocalDate;

public record ProductResponse(
        String id,
        String name,
        String description,
        String sector,
        Double value,
        LocalDate date,
        Integer quantidade) {

}
