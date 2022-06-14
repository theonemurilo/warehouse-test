package com.murilo.test.warehouse.controller

import com.murilo.test.warehouse.service.ArticleService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters.fromMultipartData
import reactor.core.publisher.Mono

@WebFluxTest(controllers = [InventoryController::class])
@Import(InventoryControllerMockConfig::class)
internal class InventoryControllerTest {

    @Autowired
    private lateinit var articleService: ArticleService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun `given an inventory json file then it should be uploaded`() {
        every { articleService.saveFile(any()) } returns Mono.empty()

        webTestClient.post()
            .uri { uriBuilder ->
                uriBuilder.path("/inventories")
                    .queryParam("uploadFile")
                    .build()
            }
            .body(fromMultipartData("files", this::class.java.getResource("/test.txt")!!))
            .exchange()
            .expectStatus().isCreated

        verify(exactly = 1) { articleService.saveFile(any()) }
    }

}

class InventoryControllerMockConfig {

    @Bean
    @Primary
    fun articleService(): ArticleService = mockk()
}