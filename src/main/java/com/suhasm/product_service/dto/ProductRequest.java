package com.suhasm.product_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank
        String name,

        String description,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal price,

        @NotNull
        @PositiveOrZero
        Integer stock,

        @NotBlank
        String sku,

        @NotNull
        Long categoryId
) {
}