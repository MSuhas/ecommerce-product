package com.suhasm.product_service.controller;

import com.suhasm.product_service.dto.*;
import com.suhasm.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(
            @Valid @RequestBody ProductRequest request) {

        return productService.create(request);
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable Long id) {

        return productService.findById(id);
    }

    @PostMapping("/{id}/reserve-stock")
    public ResponseEntity<ReserveStockResponse> reserveStock(
            @PathVariable Long id,
            @Valid @RequestBody ReserveStockRequest request) {

        ReserveStockResponse response =
                productService.reserveStock(id, request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/release-stock")
    public ReleaseStockResponse releaseStock(
            @PathVariable Long id,
            @Valid @RequestBody ReleaseStockRequest request) {

        return productService.releaseStock(id, request);
    }
}