package com.genial.demo.shared;

import java.time.LocalDate;

public record ProductNestedResponse(
        String id,
        StorageResponse storageResponse,
        String name,
        String description,
        String sector,
        Double value,
        LocalDate date,
        Integer quantidade) {

}
