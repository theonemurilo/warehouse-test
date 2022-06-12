package com.murilo.test.warehouse.composer

import com.murilo.test.warehouse.domain.Article
import com.murilo.test.warehouse.domain.Product
import com.murilo.test.warehouse.repository.ArticleRepository
import com.murilo.test.warehouse.repository.ProductRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Flux.fromIterable
import reactor.core.publisher.Mono

@Component
@Transactional(readOnly = true)
class ProductComposer(
    private val productRepository: ProductRepository,
    private val articleRepository: ArticleRepository,
) {

    fun isProductSellable(product: Product): Mono<Boolean> =
        fromIterable(product.productArticles)
            .map { it.articleId }
            .collectList()
            .flatMapMany { articleRepository.findAllById(it) }
            .collectList()
            .map { articles -> articles.all { article -> product.articleHaveStock(article) } }

    fun findAllProductsPaged(pageable: Pageable): Mono<Pair<List<Product>, List<Article>>> =
        productRepository.findByProductNumberNotNull(pageable)
            .collectList()
            .flatMap { findProductArticles(it) }

    private fun findProductArticles(products: List<Product>): Mono<Pair<List<Product>, List<Article>>> =
        findArticles(products)
            .collectList()
            .map { Pair(products, it) }

    private fun findArticles(products: List<Product>) =
        fromIterable(products.flatMap { product -> product.productArticles.map { it.articleId } })
            .collectList()
            .flatMapMany { articleRepository.findAllById(it) }

}
