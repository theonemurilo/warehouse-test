package com.murilo.test.warehouse.controller.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import com.murilo.test.warehouse.domain.Article

@JsonRootName(value = "inventory")
data class ArticleRequest(
    @JsonProperty("art_id")
    val articleId: Long? = null,
    val name: String? = null,
    val stock: Int? = null,
    @JsonProperty("min_stock")
    val minStock: Int? = null,
) {

    fun toDomain() = Article(
        articleId = articleId!!,
        name = name!!,
        stock = stock!!,
        minStock = minStock!!
    )
}
