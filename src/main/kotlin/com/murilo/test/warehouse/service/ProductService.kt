package com.murilo.test.warehouse.service

import com.murilo.test.warehouse.composer.ProductComposer
import com.murilo.test.warehouse.controller.response.ProductArticleResponse
import com.murilo.test.warehouse.controller.response.ProductResponse
import com.murilo.test.warehouse.domain.Article
import com.murilo.test.warehouse.domain.Product
import com.murilo.test.warehouse.domain.ProductArticle
import com.murilo.test.warehouse.exceptions.ProductNotFoundException
import com.murilo.test.warehouse.exceptions.UnprocessableEntityException
import com.murilo.test.warehouse.repository.ProductRepository
import org.springframework.data.domain.PageRequest.of
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
@Transactional
class ProductService(
    private val productRepository: ProductRepository,
    private val productComposer: ProductComposer,
    private val articleService: ArticleService,
) {

    fun sell(productNumber: Long) = productRepository.findById(productNumber)
        .switchIfEmpty { productNotFoundError(productNumber) }
        .filterWhen { productComposer.isProductSellable(it) }
        .switchIfEmpty { productNotSellableError(productNumber) }
        .flatMapMany { confirmSell(it) }
        .then()

    fun getAll(page: Int, size: Int) = productComposer.findAllProductsPaged(of(page, size))
        .map { pair -> mapProductArticles(pair.first, pair.second) }

    private fun confirmSell(product: Product) = articleService.subtractInventory(product)

    private fun mapProductArticles(products: List<Product>, articles: List<Article>) =
        articles.associateBy { it.articleId }.let { articleMap ->
            products.map { product -> mapProductResponse(product, pairProductArticle(product, articleMap)) }
        }
    private fun mapProductResponse(product: Product, productArticlePairList: List<Pair<ProductArticle, Article>>) =
        ProductResponse(
            name = product.name,
            price = product.price,
            productArticles = productArticlePairList.map {
                ProductArticleResponse(
                    articleId = it.first.articleId,
                    amountOf = it.first.amountOf,
                    availableOnStock = it.second.stock)
            },
            sellable = productArticlePairList.map { it.second }.all { it.articleHaveMinStock() }
        )

    private fun pairProductArticle(product: Product, articleMap: Map<Long, Article>) =
        mutableListOf<Pair<ProductArticle, Article>>().also { productArticlePairList ->
            product.productArticles.forEach { productArticle ->
                articleMap[productArticle.articleId]?.let {
                    productArticlePairList.add(Pair(productArticle, it))
                }
            }
        }.toList()

    private fun productNotSellableError(productNumber: Long): Mono<Product> = Mono.error(UnprocessableEntityException(
        "productNumber=$productNumber is not sellable because one or more of its articles doesn't have enough stock!"
    ))

    private fun productNotFoundError(productNumber: Long): Mono<Product> = Mono.error(ProductNotFoundException(
        "productNumber=$productNumber not found!"
    ))
}
