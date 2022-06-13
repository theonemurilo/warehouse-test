package com.murilo.test.warehouse.fixture

import com.murilo.test.warehouse.domain.Product
import com.murilo.test.warehouse.domain.ProductArticle
import java.math.BigDecimal

fun getProduct(articleId: Long = 1) = Product(
    name = "dummy product 1",
    price = BigDecimal(10),
    productArticles = listOf(ProductArticle(articleId = articleId, amountOf = 10))
)

fun getProduct(productNumber: String, articleId: Long = 1) = Product(
    productNumber = productNumber,
    name = "dummy product 1",
    price = BigDecimal(10),
    productArticles = listOf(ProductArticle(articleId = articleId, amountOf = 10))
)

fun getProductWith2Articles(articleId: Long = 2) = Product(
    name = "dummy product 1",
    price = BigDecimal(10),
    productArticles = listOf(
        ProductArticle(articleId = articleId, amountOf = 10),
        ProductArticle(articleId = articleId + 1, amountOf = 10))
)

fun getProduct(articleId: Long = 1, amountOf: Int) = Product(
    name = "dummy product 1",
    price = BigDecimal(10),
    productArticles = listOf(ProductArticle(articleId = articleId, amountOf = amountOf))
)