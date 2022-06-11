package com.murilo.test.warehouse.controller.response

import java.math.BigDecimal

data class ProductResponse(
    val name: String,
    val price: BigDecimal,
    val productArticles: List<ProductArticleResponse>,
    val sellable: Boolean,
)
