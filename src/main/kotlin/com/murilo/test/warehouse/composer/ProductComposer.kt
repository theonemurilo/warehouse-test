package com.murilo.test.warehouse.composer

import com.murilo.test.warehouse.controller.response.ProductArticleResponse
import com.murilo.test.warehouse.controller.response.ProductResponse
import com.murilo.test.warehouse.domain.Article
import com.murilo.test.warehouse.domain.Article.Companion.emptyArticle
import com.murilo.test.warehouse.domain.Product
import com.murilo.test.warehouse.domain.ProductArticle
import com.murilo.test.warehouse.repository.ArticleRepository
import com.murilo.test.warehouse.repository.ProductRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
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
            .map { articles -> articles.all { article -> product.articleHasStock(article) } }

    fun findAllProductsPaged(pageable: Pageable): Mono<List<ProductResponse>> =
        productRepository.findByProductNumberNotNull(pageable)
            .collectList()
            .flatMap { findProductArticles(it) }
            .map { pair -> mapProductArticles(pair.first, pair.second) }

    private fun findProductArticles(products: List<Product>): Mono<Pair<List<Product>, List<Article>>> =
        findArticles(products)
            .collectList()
            .map { Pair(products, it) }

    private fun findArticles(products: List<Product>) =
        fromIterable(products.flatMap { product -> product.productArticles.map { it.articleId } })
            .collectList()
            .flatMapMany { articleRepository.findAllById(it).defaultIfEmpty(emptyArticle()) }

    private fun mapProductArticles(products: List<Product>, articles: List<Article>) =
        articles.associateBy { it.articleId }.let { articleMap ->
            products.map { product -> mapProductResponse(product, pairProductArticle(product, articleMap)) }
        }

    private fun mapProductResponse(product: Product, productArticlePairList: List<Pair<ProductArticle, Article>>) =
        ProductResponse(
            productNumber = product.productNumber!!,
            name = product.name,
            price = product.price,
            productArticles = productArticlePairList.map {
                ProductArticleResponse(
                    articleId = it.first.articleId,
                    amountOf = it.first.amountOf,
                    availableOnStock = it.second.stock)
            },
            sellable = productArticlePairList.map { it.second }.all { product.articleHasStock(it) }
        )

    private fun pairProductArticle(product: Product, articleMap: Map<Long, Article>) =
        mutableListOf<Pair<ProductArticle, Article>>().also { productArticlePairList ->
            product.productArticles.forEach { productArticle ->
                if (articleMap[productArticle.articleId] != null) {
                    productArticlePairList.add(Pair(productArticle, articleMap[productArticle.articleId]!!))
                } else {
                    productArticlePairList.add(Pair(productArticle, productArticle.toArticle()))
                }
            }
        }.toList()

}
