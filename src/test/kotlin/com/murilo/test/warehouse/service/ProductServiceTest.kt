package com.murilo.test.warehouse.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.murilo.test.warehouse.composer.ProductComposer
import com.murilo.test.warehouse.domain.Product
import com.murilo.test.warehouse.exceptions.NotFoundException
import com.murilo.test.warehouse.exceptions.UnprocessableEntityException
import com.murilo.test.warehouse.fixture.getJsonPayload
import com.murilo.test.warehouse.fixture.getProduct
import com.murilo.test.warehouse.fixture.getProductRequest
import com.murilo.test.warehouse.fixture.getProductResponse
import com.murilo.test.warehouse.repository.ProductRepository
import com.murilo.test.warehouse.utils.readFile
import io.kotest.assertions.forEachAsClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux.just
import reactor.core.publisher.Mono
import reactor.test.StepVerifier.create
import java.math.BigDecimal

internal class ProductServiceTest {

    private val productRepository = mockk<ProductRepository>()
    private val productComposer = mockk<ProductComposer>()
    private val articleService = mockk<ArticleService>()
    private val mapper = jacksonObjectMapper()

    private val productService = ProductService(
        productRepository,
        productComposer,
        articleService,
        mapper
    )

    @Test
    fun `given a product with articles when selling it then it should be subtracted from inventory`() {
        val product = getProduct(productNumber = "123")
        every { productRepository.findById(product.productNumber!!) } returns Mono.just(product)
        every { productComposer.isProductSellable(product) } returns Mono.just(true)
        every { articleService.subtractInventory(product) } returns Mono.empty()

        productService.sell("123").block()

        verify(exactly = 1) { articleService.subtractInventory(product) }
    }

    @Test
    fun `given a non existent product then it should return not found error`() {
        val product = getProduct(productNumber = "123")
        every { productRepository.findById(product.productNumber!!) } returns Mono.empty()

        create(productService.sell("123"))
            .expectError(NotFoundException::class.java)
            .verify()
    }

    @Test
    fun `given a not sellable product then it should return unprocessable entity error`() {
        val product = getProduct(productNumber = "123")
        every { productRepository.findById(product.productNumber!!) } returns Mono.just(product)
        every { productComposer.isProductSellable(product) } returns Mono.just(false)

        create(productService.sell("123"))
            .expectError(UnprocessableEntityException::class.java)
            .verify()
    }

    @Test
    fun `should return all products paged`() {
        every {
            productComposer.findAllProductsPaged(PageRequest.of(0, 1))
        } returns Mono.just(listOf(getProductResponse()))

        val productResponses = productService.getAll(0, 1).block()

        productResponses shouldNotBe null
        productResponses!! shouldHaveSize 1
        productResponses.forEachAsClue {
            it.name shouldBe "dummy name"
            it.sellable shouldBe true
            it.price shouldBe BigDecimal(10.0)
            it.productNumber shouldBe "123"
            it.productArticles.forEachAsClue { productArticle ->
                productArticle.articleId shouldBe 1
                productArticle.amountOf shouldBe 1
                productArticle.availableOnStock shouldBe 1
            }
        }
    }

    @Test
    fun `should save the product input file in the database`() {
        val filePartFlux = just(mockk<FilePart>(relaxed = true))
        val payload = getJsonPayload()
        val product = getProduct()
        val productsSlot = slot<List<Product>>()
        mockkStatic("com.murilo.test.warehouse.utils.FileReaderKt")
        every { readFile(filePartFlux) } returns Mono.just(payload)
        every { productRepository.saveAll(any<Iterable<Product>>()) } returns just(getProduct())

        productService.saveFile(filePartFlux).block()

        verify(exactly = 1) { productRepository.saveAll(capture(productsSlot)) }
        productsSlot.captured.forEachAsClue {
            it.name shouldBe product.name
            it.price shouldBe product.price
            it.version shouldBe null

            it.productArticles.forEachAsClue { productArticle ->
                productArticle.articleId shouldBe product.productArticles.first().articleId
                productArticle.amountOf shouldBe product.productArticles.first().amountOf
            }
        }
    }
}