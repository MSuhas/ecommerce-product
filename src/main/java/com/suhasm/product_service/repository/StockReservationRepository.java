package com.suhasm.product_service.repository;

import com.suhasm.product_service.entity.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockReservationRepository
        extends JpaRepository<StockReservation, Long> {
    Optional<StockReservation> findByReservationId(String reservationId);
}
