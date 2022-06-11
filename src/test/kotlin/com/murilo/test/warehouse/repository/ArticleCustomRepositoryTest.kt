package com.murilo.test.warehouse.repository

import com.murilo.test.warehouse.fixture.getArticle
import com.murilo.test.warehouse.fixture.getArticles
import com.murilo.test.warehouse.fixture.getProduct
import com.murilo.test.warehouse.fixture.getProductWith2Articles
import io.kotest.assertions.forEachAsClue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import

@DataMongoTest
@Import(ArticleCustomRepository::class)
internal class ArticleCustomRepositoryTest {

    @Autowired
    private lateinit var articleCustomRepository: ArticleCustomRepository

    @Autowired
    private lateinit var articleRepository: ArticleRepository

    @BeforeEach
    fun setup() {
        articleRepository.save(getArticle()).block()
    }

    @Test
    fun `given an inventory with only 1 article then it should subtract the stock by the respective product article stock`() {
        val article = getArticle()

        val updateResult = articleCustomRepository.subtractArticlesFromStockByProduct(getProduct()).block()

        val articleUpdated = articleRepository.findById(article.articleId).block()
        updateResult!!.modifiedCount shouldBe 1
        articleUpdated!!.stock shouldBe 0
    }

    @Test
    fun `given an inventory with some articles then it should subtract the stock by the respective product articles stock`() {
        val articles = getArticles()
        val product = getProductWith2Articles()
        articleRepository.saveAll(getArticles()).collectList().block()

        val updateResult = articleCustomRepository.subtractArticlesFromStockByProduct(product).block()

        val articlesUpdated = articleRepository.findAllById(articles.map { it.articleId }).collectList().block()
        updateResult!!.modifiedCount shouldBe articles.size
        articlesUpdated!!.forEachAsClue {
            it.stock shouldBe 0
        }
    }

    @Test
    fun `given an inventory with some articles and a selling product with no articles then it should subtract nothing of the stock`() {
        val article = getArticle()
        val product = getProduct().copy(productArticles = listOf())

        val updateResult = articleCustomRepository.subtractArticlesFromStockByProduct(product).block()

        val articleUpdated = articleRepository.findById(article.articleId).block()
        updateResult shouldBe null
        articleUpdated!!.stock shouldBe article.stock
    }

    @AfterEach
    fun cleanup() {
        articleRepository.deleteAll().block()
    }
}