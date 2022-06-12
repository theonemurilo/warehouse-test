package com.murilo.test.warehouse.repository

import com.murilo.test.warehouse.domain.Product
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ProductRepository : ReactiveCrudRepository<Product, Long> {
    fun findByProductNumberNotNull(pageable: Pageable): Flux<Product>
}