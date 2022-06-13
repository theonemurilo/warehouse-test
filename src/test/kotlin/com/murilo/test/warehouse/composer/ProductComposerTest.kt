package com.murilo.test.warehouse.composer

import com.murilo.test.warehouse.controller.response.ProductResponse
import com.murilo.test.warehouse.domain.Article
import com.murilo.test.warehouse.domain.Product
import com.murilo.test.warehouse.fixture.getArticle
import com.murilo.test.warehouse.fixture.getProduct
import com.murilo.test.warehouse.repository.ArticleRepository
import com.murilo.test.warehouse.repository.ProductRepository
import io.kotest.assertions.asClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest.of
import reactor.core.publisher.Flux

internal class ProductComposerTest {

    private val productRepository = mockk<ProductRepository>()
    private val articleRepository = mockk<ArticleRepository>()
    private val composer = ProductComposer(productRepository, articleRepository)

    @Test
    fun `given a product with all of its articles available in stock when calling isProductSellable() then it should return true`() {
        val product = getProduct(1)
        val article = getArticle()
        every { articleRepository.findAllById(listOf(article.articleId)) } returns Flux.just(article)

        val productSellable = composer.isProductSellable(product).block()

        productSellable shouldBe true
    }

    @Test
    fun `given a product with none of its articles available in stock when calling isProductSellable() then it should return false`() {
        val product = getProduct(1)
        val requiredStock = product.productArticles.first().amountOf - 1
        val article = getArticle().copy(stock = requiredStock, minStock = requiredStock)
        every { articleRepository.findAllById(listOf(article.articleId)) } returns Flux.just(article)

        val productSellable = composer.isProductSellable(product).block()

        productSellable shouldBe false
    }

    @Test
    fun `given a product without minimum stock for its article when calling isProductSellable() then it should return false`() {
        val product = getProduct(1)
        val requiredStock = product.productArticles.first().amountOf
        val article = getArticle().copy(stock = requiredStock + 9, minStock = requiredStock + 10)
        every { articleRepository.findAllById(listOf(article.articleId)) } returns Flux.just(article)

        val productSellable = composer.isProductSellable(product).block()

        productSellable shouldBe false
    }

    @Test
    fun `should find all products with their articles paged`() {
        val product = getProduct(productNumber = "1", articleId = 1)
        val article = getArticle(articleId = 1)
        every { productRepository.findByProductNumberNotNull(of(0, 1)) } returns Flux.just(product)
        every { articleRepository.findAllById(listOf(article.articleId)) } returns Flux.just(article)

        val productResponses = composer.findAllProductsPaged(of(0, 1)).block()!!

        productResponses shouldNotBe null
        productResponses shouldHaveSize 1
        assertProduct(productResponses.first(), product, article)
    }

    @Test
    fun `should find all products without their articles paged`() {
        val product = getProduct(productNumber = "1", articleId = 1)
        val article = getArticle(articleId = 1)
        every { productRepository.findByProductNumberNotNull(of(0, 1)) } returns Flux.just(product)
        every { articleRepository.findAllById(listOf(article.articleId)) } returns Flux.empty()

        val productResponses = composer.findAllProductsPaged(of(0, 1)).block()!!

        productResponses shouldNotBe null
        productResponses shouldHaveSize 1
        assertProductWithoutArticles(productResponses.first(), product)
    }

    @Test
    fun `should find all products with their articles paged when there's no stock`() {
        val product = getProduct(productNumber = "1", articleId = 1)
        val requiredStock = product.productArticles.first().amountOf - 1
        val article = getArticle(articleId = 1, stock = requiredStock)
        every { productRepository.findByProductNumberNotNull(of(0, 1)) } returns Flux.just(product)
        every { articleRepository.findAllById(listOf(article.articleId)) } returns Flux.just(article)

        val productResponses = composer.findAllProductsPaged(of(0, 1)).block()!!

        productResponses shouldNotBe null
        productResponses shouldHaveSize 1
        assertProductWithoutStock(productResponses.first(), product, requiredStock)
    }

    @Test
    fun `should find all products with their articles paged when there's no minimum stock`() {
        val product = getProduct(productNumber = "1", articleId = 1)
        val requiredStock = product.productArticles.first().amountOf + 10
        val requiredMinStock = product.productArticles.first().amountOf + 11
        val article = getArticle(articleId = 1, stock = requiredStock, minStock = requiredMinStock)
        every { productRepository.findByProductNumberNotNull(of(0, 1)) } returns Flux.just(product)
        every { articleRepository.findAllById(listOf(article.articleId)) } returns Flux.just(article)

        val productResponses = composer.findAllProductsPaged(of(0, 1)).block()!!

        productResponses shouldNotBe null
        productResponses shouldHaveSize 1
        assertProductWithoutStock(productResponses.first(), product, requiredStock)
    }

    private fun assertProduct(
        productResponse: ProductResponse?,
        product: Product,
        article: Article,
    ) {
        productResponse shouldNotBe null
        productResponse?.asClue {
            it.productNumber shouldBe product.productNumber
            it.price shouldBe product.price
            it.name shouldBe product.name
            it.sellable shouldBe true

            it.productArticles shouldHaveSize 1
            it.productArticles.first().asClue { productArticle ->
                productArticle.articleId shouldBe product.productArticles.first().articleId
                productArticle.amountOf shouldBe product.productArticles.first().amountOf
                productArticle.availableOnStock shouldBe article.stock
            }
        }
    }

    private fun assertProductWithoutArticles(
        productSaved: ProductResponse?,
        product: Product,
    ) {
        productSaved shouldNotBe null
        productSaved?.asClue {
            it.productNumber shouldBe product.productNumber
            it.price shouldBe product.price
            it.name shouldBe product.name
            it.sellable shouldBe false

            it.productArticles shouldHaveSize 1
            it.productArticles.first().asClue { productArticle ->
                productArticle.articleId shouldBe product.productArticles.first().articleId
                productArticle.amountOf shouldBe product.productArticles.first().amountOf
                productArticle.availableOnStock shouldBe 0
            }
        }
    }

    private fun assertProductWithoutStock(
        productSaved: ProductResponse?,
        product: Product,
        availableStock: Int,
    ) {
        productSaved shouldNotBe null
        productSaved?.asClue {
            it.productNumber shouldBe product.productNumber
            it.price shouldBe product.price
            it.name shouldBe product.name
            it.sellable shouldBe false

            it.productArticles shouldHaveSize 1
            it.productArticles.first().asClue { productArticle ->
                productArticle.articleId shouldBe product.productArticles.first().articleId
                productArticle.amountOf shouldBe product.productArticles.first().amountOf
                productArticle.availableOnStock shouldBe availableStock
            }
        }
    }
}