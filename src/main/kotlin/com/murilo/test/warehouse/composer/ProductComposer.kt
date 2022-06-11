package com.murilo.test.warehouse.composer

import com.murilo.test.warehouse.domain.Product
import com.murilo.test.warehouse.repository.ArticleRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux.fromIterable
import reactor.core.publisher.Mono

@Component
@Transactional(readOnly = true)
class ProductComposer(
    private val articleRepository: ArticleRepository,
) {

    fun isProductSellable(product: Product): Mono<Boolean> =
        fromIterable(product.productArticles)
            .map { it.articleId }
            .collectList()
            .flatMapMany { articleRepository.findAllById(it) }
            .collectList()
            .map { articles -> articles.all { article -> product.articleHaveStock(article) } }

}
