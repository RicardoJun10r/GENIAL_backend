package com.genial.demo.shared;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductNestedResponse(
        String id,
        StorageResponse storage,
        String name,
        String description,
        String sector,
        BigDecimal value,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer quantidade) {

}
