package com.murilo.test.warehouse.controller.request

import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class ProductArticlesRequestTest {

    @Test
    fun `given an product articles request then it should be converted to its domain object`() {
        val productArticlesRequest = ProductArticlesRequest(articleId = 1, amountOf = 1)

        val productArticle = productArticlesRequest.toDomain()

        productArticle.asClue {
            it.articleId shouldBe productArticlesRequest.articleId
            it.amountOf shouldBe productArticlesRequest.amountOf
        }
    }
}