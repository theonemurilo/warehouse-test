package com.murilo.test.warehouse.controller.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import com.murilo.test.warehouse.domain.Product
import java.math.BigDecimal

@JsonRootName(value = "product")
data class ProductRequest(
    val name: String? = null,
    val price: BigDecimal? = null,
    @JsonProperty("contain_articles")
    val productArticles: List<ProductArticlesRequest> = emptyList(),
) {

    fun toDomain() = Product(
        name = name!!,
        price = price!!,
        productArticles = productArticles.map { it.toDomain() }
    )
}