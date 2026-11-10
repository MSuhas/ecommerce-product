package com.suhasm.product_service.dto;

import java.math.BigDecimal;

public record ReserveStockResponse(
        Long productId,
        Integer reservedQuantity,
        Integer remainingStock,
        String productName,
        BigDecimal unitPrice
) {
}