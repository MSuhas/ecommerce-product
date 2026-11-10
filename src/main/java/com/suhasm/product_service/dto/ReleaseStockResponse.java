package com.suhasm.product_service.dto;

public record ReleaseStockResponse(
        Long productId,
        Integer releasedQuantity,
        Integer currentStock
) {}
