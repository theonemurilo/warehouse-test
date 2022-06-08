package com.murilo.test.warehouse

import com.murilo.test.warehouse.composer.ProductComposer
import com.murilo.test.warehouse.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val productComposer: ProductComposer,
) {
}