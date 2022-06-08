package com.murilo.test.warehouse.domain

import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@Document("products")
data class Product(
    @Indexed
    val productNumber: Long,
    val name: String,
    val price: BigDecimal,
    val productArticles: List<ProductArticle>
) {

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
