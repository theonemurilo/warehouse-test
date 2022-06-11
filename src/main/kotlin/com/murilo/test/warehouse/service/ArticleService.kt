package com.murilo.test.warehouse.service

import com.murilo.test.warehouse.domain.Product
import com.murilo.test.warehouse.repository.ArticleCustomRepository
import org.springframework.stereotype.Service

@Service
class ArticleService(
    private val customRepository: ArticleCustomRepository,
) {

    fun subtractInventory(product: Product) = customRepository.subtractArticlesFromStockByProduct(product)
}