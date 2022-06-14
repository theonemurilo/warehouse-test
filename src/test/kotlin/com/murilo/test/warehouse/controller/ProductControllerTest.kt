package com.murilo.test.warehouse.controller

import com.murilo.test.warehouse.exceptions.NotFoundException
import com.murilo.test.warehouse.fixture.getProductResponse
import com.murilo.test.warehouse.service.ProductService
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters.fromMultipartData
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.error

@WebFluxTest(controllers = [ProductController::class])
@Import(ProductControllerMockConfig::class)
internal class ProductControllerTest {

    @Autowired
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun setup() {
        clearMocks(productService)
    }

    @Test
    fun `given an product number then it should be sold`() {
        val productNumber = "123"
        every { productService.sell(productNumber) } returns Mono.empty()

        webTestClient.put()
            .uri("/products/$productNumber?sell")
            .exchange()
            .expectStatus().isOk

        verify(exactly = 1) { productService.sell(productNumber) }
    }

    @Test
    fun `given an product number not existent then it should return not found http status`() {
        val productNumber = "123"
        every { productService.sell(productNumber) } returns error(NotFoundException("productNumber=$productNumber not found!"))

        webTestClient.put()
            .uri("/products/$productNumber?sell")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("productNumber=$productNumber not found!")
            .jsonPath("$.code").isEqualTo(NOT_FOUND.value())

        verify(exactly = 1) { productService.sell(productNumber) }
    }

    @Test
    fun `should return all products`() {
        val productResponse = getProductResponse()
        every { productService.getAll(0, 1) } returns Mono.just(listOf(productResponse))

        webTestClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/products")
                    .queryParam("page", 0)
                    .queryParam("size", 1)
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].productNumber").isEqualTo(productResponse.productNumber)
            .jsonPath("$[0].name").isEqualTo(productResponse.name)
            .jsonPath("$[0].price").isEqualTo(productResponse.price)
            .jsonPath("$[0].sellable").isEqualTo(productResponse.sellable)
            .jsonPath("$[0].productArticles[0].articleId").isEqualTo(productResponse.productArticles.first().articleId)
            .jsonPath("$[0].productArticles[0].amountOf").isEqualTo(productResponse.productArticles.first().amountOf)
            .jsonPath("$[0].productArticles[0].availableOnStock").isEqualTo(productResponse.productArticles.first().availableOnStock)

        verify(exactly = 1) { productService.getAll(0, 1) }
    }

    @Test
    fun `given an product json file then it should be uploaded`() {
        every { productService.saveFile(any()) } returns Mono.empty()

        webTestClient.post()
            .uri { uriBuilder ->
                uriBuilder.path("/products")
                    .queryParam("uploadFile")
                    .build()
            }
            .body(fromMultipartData("files", this::class.java.getResource("/test.txt")!!))
            .exchange()
            .expectStatus().isCreated

        verify(exactly = 1) { productService.saveFile(any()) }
    }

    @Test
    fun `given an product json file when an unknown problem occurs then it should return internal server error`() {
        every { productService.saveFile(any()) } returns error(RuntimeException("weird error"))

        webTestClient.post()
            .uri { uriBuilder ->
                uriBuilder.path("/products")
                    .queryParam("uploadFile")
                    .build()
            }
            .body(fromMultipartData("files", this::class.java.getResource("/test.txt")!!))
            .exchange()
            .expectStatus().isEqualTo(INTERNAL_SERVER_ERROR.value())
            .expectBody()
            .jsonPath("$.message").isEqualTo("weird error")
            .jsonPath("$.code").isEqualTo(INTERNAL_SERVER_ERROR.value())


        verify(exactly = 1) { productService.saveFile(any()) }
    }
}

class ProductControllerMockConfig {

    @Bean
    @Primary
    fun productService(): ProductService = mockk()
}