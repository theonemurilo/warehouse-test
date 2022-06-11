package com.murilo.test.warehouse.repository

import com.murilo.test.warehouse.domain.Product
import com.murilo.test.warehouse.fixture.getProduct
import io.kotest.assertions.asClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest

@DataMongoTest
internal class ProductRepositoryTest {

    @Autowired
    private lateinit var productRepository: ProductRepository

    @BeforeEach
    fun setup() {
        productRepository.save(getProduct()).block()
    }

    @Test
    fun `given a saved product and its articles then it should be possible to find it`() {
        val productFound = productRepository.findById(getProduct().productNumber).block()

        assertProduct(productFound, getProduct())
    }

    @Test
    fun `given a saved product and its articles then it should be possible to update it`() {
        val productFound = productRepository.findById(getProduct().productNumber).block()
        val newProduct = productFound!!.copy(name = "dummy product 2")

        val productUpdated = productRepository.save(newProduct).block()

        assertProduct(productUpdated, getProduct().copy(name = "dummy product 2"))
    }

    @Test
    fun `given a saved product and its articles then it should be possible to delete it`() {
        val productFound = productRepository.findById(getProduct().productNumber).block()

        productRepository.deleteById(productFound!!.productNumber).block()

        val productAfterDelete = productRepository.findById(productFound.productNumber).block()

        productAfterDelete shouldBe null
    }

    @AfterEach
    fun cleanup() {
        productRepository.deleteAll().block()
    }

    private fun assertProduct(
        productSaved: Product?,
        product: Product,
    ) {
        productSaved shouldNotBe null
        productSaved?.asClue {
            it.productNumber shouldBe product.productNumber
            it.price shouldBe product.price
            it.name shouldBe product.name

            it.productArticles shouldHaveSize 1
            it.productArticles.first().asClue { productArticle ->
                productArticle.articleId shouldBe product.productArticles.first().articleId
                productArticle.amountOf shouldBe product.productArticles.first().amountOf
            }
        }
    }
}
