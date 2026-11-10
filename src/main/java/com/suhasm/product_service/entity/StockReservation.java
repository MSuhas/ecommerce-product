package com.suhasm.product_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "stock_reservation",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_stock_reservation_reservation_id",
                        columnNames = "reservation_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String reservationId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StockReservationStatus status;

    @Column(nullable = false)
    private Instant createdAt;
}
