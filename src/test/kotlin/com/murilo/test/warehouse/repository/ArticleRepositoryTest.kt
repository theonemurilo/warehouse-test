package com.murilo.test.warehouse.repository

import com.murilo.test.warehouse.domain.Article
import com.murilo.test.warehouse.fixture.getArticle
import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest

@DataMongoTest
internal class ArticleRepositoryTest {

    @Autowired
    private lateinit var articleRepository: ArticleRepository
    private lateinit var article: Article

    @BeforeEach
    fun setup() {
        article = articleRepository.save(getArticle()).block()!!
    }

    @Test
    fun `given a saved article then it should be possible to find it`() {
        val foundArticle = articleRepository.findById(article.articleId).block()

        assertArticle(foundArticle, article)
    }

    @Test
    fun `given a saved article then it should be possible to update it`() {
        val newArticle = article.copy(name = "new dummy article")

        articleRepository.save(newArticle).block()
        val updatedArticle = articleRepository.findById(newArticle.articleId).block()

        assertArticle(updatedArticle, newArticle)
    }

    @Test
    fun `given a saved article then it should be possible to delete it`() {
        articleRepository.deleteById(article.articleId).block()
        val deletedArticle = articleRepository.findById(article.articleId).block()

        deletedArticle shouldBe null
    }

    @AfterEach
    fun cleanup() {
        articleRepository.deleteAll().block()
    }

    private fun assertArticle(
        foundArticle: Article?,
        article: Article,
    ) {
        foundArticle shouldNotBe null
        foundArticle?.asClue {
            it.articleId shouldBe article.articleId
            it.stock shouldBe article.stock
            it.name shouldBe article.name
            it.minStock shouldBe article.minStock
        }
    }
}