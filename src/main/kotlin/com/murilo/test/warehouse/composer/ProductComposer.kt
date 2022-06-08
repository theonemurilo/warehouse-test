package com.murilo.test.warehouse.composer

import com.murilo.test.warehouse.repository.ArticleRepository
import com.murilo.test.warehouse.repository.ProductRepository
import org.springframework.stereotype.Component

@Component
class ProductComposer(
    private val productRepository: ProductRepository,
    private val articleRepository: ArticleRepository,
) {


}
