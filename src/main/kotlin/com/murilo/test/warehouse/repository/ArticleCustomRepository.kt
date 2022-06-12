package com.murilo.test.warehouse.repository

import com.mongodb.client.model.UpdateOneModel
import com.murilo.test.warehouse.domain.Product
import com.murilo.test.warehouse.domain.ProductArticle
import org.bson.Document
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Repository
@Transactional
class ArticleCustomRepository(
    private val mongoTemplate: ReactiveMongoTemplate,
) {

    fun subtractArticlesFromStockByProduct(product: Product) =
        product.productArticles.takeIf { it.isNotEmpty() }
            ?.let {
                mongoTemplate.getCollection("articles")
                    .flatMap { collection ->
                        collection.bulkWrite(getUpdatesByArticle(product.productArticles)).toMono()
                    }
            } ?: Mono.empty()

    private fun getUpdatesByArticle(productArticles: List<ProductArticle>): List<UpdateOneModel<Document>> =
        productArticles.map {
            UpdateOneModel(Document("_id", it.articleId),
                Document("\$inc", Document("stock", -it.amountOf))
            )
        }
}