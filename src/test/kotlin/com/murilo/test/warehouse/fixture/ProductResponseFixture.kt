package com.murilo.test.warehouse.fixture

import com.murilo.test.warehouse.controller.response.ProductArticleResponse
import com.murilo.test.warehouse.controller.response.ProductResponse
import java.math.BigDecimal

fun getProductResponse() = ProductResponse(
    productNumber = "123",
    name = "dummy name",
    price = BigDecimal(10.0),
    sellable = true,
    productArticles = listOf(ProductArticleResponse(
        articleId = 1,
        amountOf = 1,
        availableOnStock = 1,
    ))
)