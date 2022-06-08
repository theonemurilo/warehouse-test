package com.murilo.test.warehouse.domain

import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document("articles")
data class Article(
    @Indexed
    val articleId: Long,
    val name: String,
    val stock: Int,
    val minStock: Int,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Article

        if (articleId != other.articleId) return false

        return true
    }

    override fun hashCode(): Int {
        return articleId.hashCode()
    }
}