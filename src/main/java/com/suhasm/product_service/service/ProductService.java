package com.suhasm.product_service.service;

import com.suhasm.product_service.dto.*;
import com.suhasm.product_service.entity.Category;
import com.suhasm.product_service.entity.Product;
import com.suhasm.product_service.entity.StockReservation;
import com.suhasm.product_service.entity.StockReservationStatus;
import com.suhasm.product_service.exception.DuplicateProductException;
import com.suhasm.product_service.exception.InsufficientStockException;
import com.suhasm.product_service.repository.CategoryRepository;
import com.suhasm.product_service.repository.ProductRepository;
import com.suhasm.product_service.repository.StockReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StockReservationRepository stockReservationRepository;

    @Transactional
    public ProductResponse create(ProductRequest request) {

        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateProductException(
                    "Product with SKU '" + request.sku() + "' already exists"
            );
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Category not found: " + request.categoryId()));

        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .category(category)
                .version(0L)
                .sku(request.sku())
                .build();

        Product saved = productRepository.save(product);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Product not found: " + id));

        return toResponse(product);
    }

    @Transactional
    public ReserveStockResponse reserveStock(
            Long productId,
            ReserveStockRequest request) {

        Optional<StockReservation> existing =
                stockReservationRepository
                        .findByReservationId(request.reservationId());

        if (existing.isPresent()) {
            StockReservation reservation = existing.get();
            if (reservation.getStatus() == StockReservationStatus.RESERVED) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Product not found: " + productId));

                return new ReserveStockResponse(
                        product.getId(),
                        reservation.getQuantity(),
                        product.getStock(),
                        product.getName(),
                        product.getPrice()
                );
            }
        }

        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Product not found: " + productId));
        if (product.getStock() < request.quantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock for product: " + productId);
        }
        product.setStock(product.getStock() - request.quantity());

        StockReservation reservation = StockReservation.builder()
                .reservationId(request.reservationId())
                .productId(productId)
                .quantity(request.quantity())
                .status(StockReservationStatus.RESERVED)
                .createdAt(Instant.now())
                .build();

        stockReservationRepository.save(reservation);

        return new ReserveStockResponse(
                product.getId(),
                request.quantity(),
                product.getStock(),
                product.getName(),
                product.getPrice()
        );

    }

    @Transactional
    public ReleaseStockResponse releaseStock(
            Long productId,
            ReleaseStockRequest request) {

        StockReservation reservation =
                stockReservationRepository
                        .findByReservationId(request.reservationId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Reservation not found: "
                                                + request.reservationId()));

        // 2. Make sure this reservation belongs to this product
        if (!reservation.getProductId().equals(productId)) {
            throw new IllegalArgumentException(
                    "Reservation does not belong to product "
                            + productId);
        }


        // Duplicate compensation
        if (reservation.getStatus() == StockReservationStatus.RELEASED) {

            Product product = productRepository.findById(productId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Product not found: " + productId));

            return new ReleaseStockResponse(
                    product.getId(),
                    reservation.getQuantity(),
                    product.getStock()
            );
        }

        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Product not found: " + productId));

        product.setStock(
                product.getStock() + reservation.getQuantity()
        );

        reservation.setStatus(
                StockReservationStatus.RELEASED
        );

        return new ReleaseStockResponse(
                product.getId(),
                reservation.getQuantity(),
                product.getStock()
        );
    }

    private ProductResponse toResponse(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getVersion()
        );
    }
}