package com.murilo.test.warehouse.controller.request

import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class ArticleRequestTest {

    @Test
    fun `given an article request then it should be converted to its domain object`() {
        val articleRequest = ArticleRequest(articleId = 1, name = "dummy name", stock = 1, minStock = 1)

        val article = articleRequest.toDomain()

        articleRequest.asClue {
            it.articleId shouldBe article.articleId
            it.name shouldBe article.name
            it.stock shouldBe article.stock
            it.minStock shouldBe article.minStock
        }
    }
}