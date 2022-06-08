package com.murilo.test.warehouse.repository

import com.murilo.test.warehouse.domain.Product
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository: ReactiveMongoRepository<Product, Long>