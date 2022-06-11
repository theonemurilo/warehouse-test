package com.murilo.test.warehouse.fixture

import com.murilo.test.warehouse.domain.Product
import com.murilo.test.warehouse.domain.ProductArticle
import java.math.BigDecimal

fun getProduct(productNumber: Long = 1, articleId: Long = 1) = Product(
    productNumber = productNumber,
    name = "dummy product 1",
    price = BigDecimal(10),
    productArticles = listOf(ProductArticle(articleId = articleId, amountOf = 10))
)

fun getProductWith2Articles(productNumber: Long = 2, articleId: Long = 2) = Product(
    productNumber = productNumber,
    name = "dummy product 1",
    price = BigDecimal(10),
    productArticles = listOf(
        ProductArticle(articleId = articleId, amountOf = 10),
        ProductArticle(articleId = articleId + 1, amountOf = 10))
)