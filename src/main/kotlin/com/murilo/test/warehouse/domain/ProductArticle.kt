package com.murilo.test.warehouse.domain

data class ProductArticle(
    val articleId: Long,
    val amountOf: Int,
) {

    fun toArticle() = Article(
        articleId = articleId,
        name = "",
        stock = 0,
        minStock = 0
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProductArticle

        if (articleId != other.articleId) return false

        return true
    }

    override fun hashCode(): Int {
        return articleId.hashCode()
    }
}
