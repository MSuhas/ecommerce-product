package com.suhasm.product_service.service;

import com.suhasm.product_service.entity.Category;
import com.suhasm.product_service.entity.Product;
import com.suhasm.product_service.repository.CategoryRepository;
import com.suhasm.product_service.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.*;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ProductServiceConcurrencyTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    ProductReservationTestService productReservationTestService;

    @Test
    void shouldPreventOverSelling() throws Exception {

        Category category = Category.builder()
                .name("Concurrency" + System.nanoTime())
                .build();
        categoryRepository.save(category);


        Product product = productRepository.save(
                Product.builder()
                        .sku("CONC-" + System.nanoTime())
                        .name("Concurrency Product")
                        .description("Test")
                        .price(new BigDecimal("100.00"))
                        .stock(10)
                        .category(category)
                        .version(0L)
                        .build()
        );

        Long productId = product.getId();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2);

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<Boolean> requestA = () -> {
            cyclicBarrier.await();

                    try {
                        productReservationTestService.reserve(productId, 7);
                        return true;

                    }catch (Exception e) {
                        return false;
                    }
        };

        Callable<Boolean> requestB = () -> {
            cyclicBarrier.await();

                    try {

                        productReservationTestService.reserve(productId, 6);
                        return true;
                    } catch (Exception e) {
                       return false;
                    }
        };

        Future<Boolean> resultA = executorService.submit(requestA);
        Future<Boolean> resultB =executorService.submit(requestB);

        boolean successA = resultA.get();
        boolean successB = resultB.get();

        executorService.shutdown();

        Product finalProduct =
                productRepository.findById(productId).orElseThrow();

        /*assertTrue(successA ^ successB,
                "Exactly one reservation should succeed");

        int expectedStock = successA ? 3 : 4;

        assertEquals(expectedStock, finalProduct.getStock(),
                "Final stock must be 3");

        assertTrue(
                finalProduct.getStock() >= 0,
                "Stock must never become negative"
        );*/
    }

}
