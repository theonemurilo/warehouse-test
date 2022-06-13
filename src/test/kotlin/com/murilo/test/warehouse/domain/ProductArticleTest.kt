package com.murilo.test.warehouse.domain

import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class ProductArticleTest {

    @Test
    fun `should return a default article`() {
        val productArticle = ProductArticle(articleId = 1, amountOf = 1)
        val fromProductArticle = productArticle.toArticle()

        fromProductArticle.asClue {
            it.articleId shouldBe fromProductArticle.articleId
            it.minStock shouldBe 0
            it.name shouldBe ""
            it.version shouldBe null
            it.stock shouldBe 0
        }
    }
}