package com.murilo.test.warehouse.domain

import com.murilo.test.warehouse.domain.Article.Companion.emptyArticle
import com.murilo.test.warehouse.fixture.getArticle
import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory


internal class ArticleTest {

    @TestFactory
    fun `article minimum stock testing`() = listOf(
        getArticle(stock = 11, minStock = 10) to true,
        getArticle(stock = 10, minStock = 10) to false,
        getArticle(stock = 9, minStock = 10) to false,
    ).map { (article, result) ->
        dynamicTest("given an article with stock=${article.stock} and minStock=${article.minStock} then calling articleHasMinStock() should return $result") {
            article.articleHasMinStock() shouldBe result
        }
    }

    @Test
    fun `should return correct empty article`() {
        val emptyArticle = emptyArticle()

        emptyArticle.asClue {
            it.articleId shouldBe -1
            it.name shouldBe ""
            it.minStock shouldBe 0
            it.stock shouldBe 0
            it.version shouldBe null
        }
    }
}