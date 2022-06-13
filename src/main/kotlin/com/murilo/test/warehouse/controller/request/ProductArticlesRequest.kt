package com.murilo.test.warehouse.controller.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.murilo.test.warehouse.domain.ProductArticle

class ProductArticlesRequest(
    @JsonProperty("art_id")
    val articleId: Long? = null,
    @JsonProperty("amount_of")
    val amountOf: Int? = null,
) {

    fun toDomain() = ProductArticle(
        articleId = articleId!!,
        amountOf = amountOf!!,
    )
}
