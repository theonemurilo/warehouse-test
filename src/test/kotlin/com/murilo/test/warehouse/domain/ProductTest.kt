package com.murilo.test.warehouse.domain

import com.murilo.test.warehouse.domain.Article.Companion.emptyArticle
import com.murilo.test.warehouse.fixture.getArticle
import com.murilo.test.warehouse.fixture.getProduct
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class ProductTest {

    @TestFactory
    fun `product articles stock testing`() = listOf(
        Triple(getProduct(amountOf = 10), getArticle(stock = 10, minStock = 9), true),
        Triple(getProduct(amountOf = 11), getArticle(stock = 10, minStock = 9), false),
        Triple(getProduct(amountOf = 10), getArticle(stock = 10, minStock = 10), false),
        Triple(getProduct(amountOf = 10).copy(productArticles = emptyList()), getArticle(stock = 10, minStock = 10), false),
        Triple(getProduct(amountOf = 10), emptyArticle(), false),
    ).map { (product, article, result) ->
        dynamicTest("given a product with an article requiring stock=${product.productArticles.firstOrNull()?.amountOf ?: 0}"
                + " and the article in the inventory has stock=${article.stock} and minimum stock=${article.minStock} then calling the method"
                + " articleHasStock() should return $result") {
            product.articleHasStock(article) shouldBe result
        }
    }

    @TestFactory
    fun `product equals testing`() = listOf(
        Triple(getProduct("1"), getProduct("1"), true),
        Triple(getProduct("1", 1), getProduct("2", 1), false),
        Triple(getProduct("1", 3), getProduct("3"), false),
        Triple(getProduct("1", 3), getArticle(), false),
        Triple(getProduct("1", 3), null, false),
    ).map { (product1, anotherObject, result) ->
        dynamicTest("given a product with articleId=${product1.productNumber} and another object=$anotherObject"
                + " then when calling equals() it should return $result") {
            (product1 == anotherObject) shouldBe result
        }
    }

    @Test
    fun `product's hashcode should be by productNumber`() {
        val productNumber = "123456"
        val product = getProduct("123456")

        product.hashCode() shouldBe productNumber.hashCode()
    }
}