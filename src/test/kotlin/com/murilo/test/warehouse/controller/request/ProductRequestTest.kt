package com.murilo.test.warehouse.controller.request

import io.kotest.assertions.asClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class ProductRequestTest {

    @Test
    fun `given an product request then it should be converted to its domain object`() {
        val productRequest = ProductRequest(
            name = "dummy product",
            price = BigDecimal(10.0),
            productArticles = listOf(ProductArticlesRequest(
                articleId = 1,
                amountOf = 1
            )))

        val product = productRequest.toDomain()

        product.asClue {
            it.price shouldBe productRequest.price
            it.name shouldBe productRequest.name

            it.productArticles shouldHaveSize 1
            it.productArticles.first().asClue { productArticle ->
                productArticle.articleId shouldBe productRequest.productArticles.first().articleId
                productArticle.amountOf shouldBe productRequest.productArticles.first().amountOf
            }
        }
    }
}