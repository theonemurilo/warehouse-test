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
import org.springframework.data.domain.PageRequest.of

@DataMongoTest
internal class ProductRepositoryTest {

    @Autowired
    private lateinit var productRepository: ProductRepository
    private lateinit var product: Product

    @BeforeEach
    fun setup() {
        product = productRepository.save(getProduct()).block()!!
    }

    @Test
    fun `given a saved product and its articles then it should be possible to find it`() {
        val productFound = productRepository.findById(product.productNumber!!).block()

        assertProduct(productFound, product)
    }

    @Test
    fun `given a saved product and its articles then it should be possible to update it`() {
        val productFound = productRepository.findById(product.productNumber!!).block()
        val newProduct = productFound!!.copy(name = "dummy product 2")

        val productUpdated = productRepository.save(newProduct).block()

        assertProduct(productUpdated, product.copy(name = "dummy product 2"))
    }

    @Test
    fun `given a saved product and its articles then it should be possible to delete it`() {
        val productFound = productRepository.findById(product.productNumber!!).block()

        productRepository.deleteById(productFound!!.productNumber!!).block()

        val productAfterDelete = productRepository.findById(productFound.productNumber!!).block()

        productAfterDelete shouldBe null
    }

    @Test
    fun `should find all pageable products when there is only one product`() {
        val allProducts = productRepository.findByProductNumberNotNull(of(0, 10)).collectList().block()

        allProducts?.size shouldBe 1
    }

    @Test
    fun `should find all pageable products when there are 5 products`() {
        productRepository.saveAll(listOf(
            getProduct(),
            getProduct(),
            getProduct(),
            getProduct(),
        )).collectList().block()

        val firstPageOfThree = productRepository.findByProductNumberNotNull(of(0, 2)).collectList().block()
        firstPageOfThree?.size shouldBe 2

        val secondPageOfThree = productRepository.findByProductNumberNotNull(of(1, 2)).collectList().block()
        secondPageOfThree?.size shouldBe 2

        val lastPagePageOfThree = productRepository.findByProductNumberNotNull(of(2, 2)).collectList().block()
        lastPagePageOfThree?.size shouldBe 1
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
