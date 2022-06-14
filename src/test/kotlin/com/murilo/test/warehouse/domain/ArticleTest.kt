package com.murilo.test.warehouse.domain

import com.murilo.test.warehouse.domain.Article.Companion.emptyArticle
import com.murilo.test.warehouse.fixture.getArticle
import com.murilo.test.warehouse.fixture.getProduct
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

    @TestFactory
    fun `article equals testing`() = listOf(
        Triple(getArticle(1), getArticle(stock = 1, minStock = 2), true),
        Triple(getArticle(2), getArticle(stock = 1, minStock = 2), false),
        Triple(getArticle(3), getArticle(stock = 1, minStock = 2), false),
        Triple(getArticle(3), getProduct(), false),
        Triple(getArticle(3), null, false),
    ).map { (article1, anotherObject, result) ->
        dynamicTest("given an article with articleId=${article1.articleId} and another object=$anotherObject"
                + " then when calling equals() it should return $result") {
            (article1 == anotherObject) shouldBe result
        }
    }

    @Test
    fun `article's hashcode should be by articleId`() {
        val articleId = 123456
        val article = getArticle(123456)

        article.hashCode() shouldBe articleId.hashCode()
    }
}