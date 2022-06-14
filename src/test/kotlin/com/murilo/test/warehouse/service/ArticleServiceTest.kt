package com.murilo.test.warehouse.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mongodb.bulk.BulkWriteResult
import com.murilo.test.warehouse.domain.Article
import com.murilo.test.warehouse.exceptions.BadRequestException
import com.murilo.test.warehouse.fixture.getArticle
import com.murilo.test.warehouse.fixture.getInventoryJsonPayload
import com.murilo.test.warehouse.fixture.getProduct
import com.murilo.test.warehouse.repository.ArticleCustomRepository
import com.murilo.test.warehouse.repository.ArticleRepository
import com.murilo.test.warehouse.utils.readFile
import io.kotest.assertions.asClue
import io.kotest.assertions.forEachAsClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import org.junit.jupiter.api.Test
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier.create

internal class ArticleServiceTest {

    private val articleRepository = mockk<ArticleRepository>()
    private val customRepository = mockk<ArticleCustomRepository>()
    private val mapper = jacksonObjectMapper()

    private val articleService = ArticleService(articleRepository, customRepository, mapper)

    @Test
    fun `should call subtract inventory`() {
        val product = getProduct()
        val bulkResult = mockk<BulkWriteResult> {
            every { modifiedCount } returns 1
        }
        every { customRepository.subtractArticlesFromStockByProduct(product) } returns Mono.just(bulkResult)

        val result = articleService.subtractInventory(product).block()

        result shouldNotBe null
        result?.asClue {
            it.modifiedCount shouldBe 1
        }
    }

    @Test
    fun `should save the inventory input file in the database`() {
        val filePartFlux = Flux.just(mockk<FilePart>(relaxed = true))
        val payload = getInventoryJsonPayload()
        val article = getArticle().copy(version = 1, articleId = 1)
        val articleSlot = slot<List<Article>>()
        mockkStatic("com.murilo.test.warehouse.utils.FileReaderKt")
        every { readFile(filePartFlux) } returns Mono.just(payload)
        every { articleRepository.saveAll(any<Iterable<Article>>()) } returns Flux.just(article)

        articleService.saveFile(filePartFlux).block()

        verify(exactly = 1) { articleRepository.saveAll(capture(articleSlot)) }
        articleSlot.captured.forEachAsClue {
            it.name shouldBe article.name
            it.version shouldBe null
            it.articleId shouldBe article.articleId
            it.stock shouldBe article.stock
            it.minStock shouldBe article.minStock
        }
    }

    @Test
    fun `should not save the inventory input file because of parsing error`() {
        val filePartFlux = Flux.just(mockk<FilePart>(relaxed = true))
        val payload = "invalid json payload"
        mockkStatic("com.murilo.test.warehouse.utils.FileReaderKt")
        every { readFile(filePartFlux) } returns Mono.just(payload)

        create(articleService.saveFile(filePartFlux))
            .expectError(BadRequestException::class.java)
            .verify()

        verify { articleRepository wasNot Called }
    }
}