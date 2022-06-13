package com.murilo.test.warehouse.domain

import com.murilo.test.warehouse.fixture.getArticle
import com.murilo.test.warehouse.fixture.getProduct
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

internal class ProductTest {

    @TestFactory
    fun `product articles stock testing`() = listOf(
        Triple(getProduct(amountOf = 10), getArticle(stock = 10, minStock = 9), true),
        Triple(getProduct(amountOf = 11), getArticle(stock = 10, minStock = 9), false),
        Triple(getProduct(amountOf = 10), getArticle(stock = 10, minStock = 10), false),
    ).map { (product, article, result) ->
        dynamicTest("given a product with an article requiring stock=${product.productArticles.first().amountOf}"
                + " and the article in the inventory has stock=${article.stock} and minimum stock=${article.minStock} then calling the method"
                + "articleHasStock() should return $result") {
            product.articleHasStock(article) shouldBe result
        }
    }
}