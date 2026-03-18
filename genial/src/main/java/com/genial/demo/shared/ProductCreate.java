package com.genial.demo.shared;

import java.math.BigDecimal;

public record ProductCreate(
                String name,
                String description,
                String sector,
                BigDecimal value,
                Integer quantidade) {

}
