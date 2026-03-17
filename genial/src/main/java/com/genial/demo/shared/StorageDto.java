package com.genial.demo.shared;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorageDto {
    private String id;
    private String name;
    private String description;
    private List<ProductResponse> products;
}
