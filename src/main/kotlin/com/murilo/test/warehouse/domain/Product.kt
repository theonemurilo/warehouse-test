package com.murilo.test.warehouse.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@Document("products")
data class Product(
    @Indexed
    @Id
    val productNumber: Long,
    val name: String,
    val price: BigDecimal,
    val productArticles: List<ProductArticle>,
    @Version
    val version: Long? = null,
) {

    @Transient
    private val productArticlesMap = productArticles.associateBy { it.articleId }

    fun articleHaveStock(article: Article) = productArticlesMap[article.articleId]
        ?.let { article.articleHaveMinStock() && article.stock >= it.amountOf }
        ?: false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Product

        if (productNumber != other.productNumber) return false

        return true
    }

    override fun hashCode(): Int {
        return productNumber.hashCode()
    }
}
