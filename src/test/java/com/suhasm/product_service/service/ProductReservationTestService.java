package com.suhasm.product_service.service;

import com.suhasm.product_service.dto.ReserveStockRequest;
import com.suhasm.product_service.dto.ReserveStockResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductReservationTestService {

    private final ProductService productService;

    @Transactional
    public ReserveStockResponse reserve(Long productId, int quantity) {
        return productService.reserveStock(productId, new ReserveStockRequest(quantity, "1"));
    }
}
