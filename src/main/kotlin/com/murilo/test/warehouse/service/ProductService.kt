package com.murilo.test.warehouse.service

import com.murilo.test.warehouse.composer.ProductComposer
import com.murilo.test.warehouse.controller.response.ProductResponse
import com.murilo.test.warehouse.domain.Product
import com.murilo.test.warehouse.exceptions.ProductNotFoundException
import com.murilo.test.warehouse.exceptions.UnprocessableEntityException
import com.murilo.test.warehouse.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
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

    private fun confirmSell(product: Product) = articleService.subtractInventory(product)

    private fun productNotSellableError(productNumber: Long): Mono<Product> = Mono.error(UnprocessableEntityException(
        "productNumber=$productNumber is not sellable because one or more of its articles doesn't have enough stock!"
    ))

    private fun productNotFoundError(productNumber: Long): Mono<Product> = Mono.error(ProductNotFoundException(
        "productNumber=$productNumber not found!"
    ))

    fun getAll(page: Int, size: Int): Flux<ProductResponse> {
        TODO("Not yet implemented")
    }
}
