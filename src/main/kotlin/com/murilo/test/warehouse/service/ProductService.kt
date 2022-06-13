package com.murilo.test.warehouse.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.murilo.test.warehouse.composer.ProductComposer
import com.murilo.test.warehouse.controller.request.ProductRequest
import com.murilo.test.warehouse.domain.Product
import com.murilo.test.warehouse.exceptions.BadRequestException
import com.murilo.test.warehouse.exceptions.NotFoundException
import com.murilo.test.warehouse.exceptions.UnprocessableEntityException
import com.murilo.test.warehouse.repository.ProductRepository
import com.murilo.test.warehouse.utils.readFile
import org.springframework.data.domain.PageRequest.of
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.error
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
@Transactional
class ProductService(
    private val productRepository: ProductRepository,
    private val productComposer: ProductComposer,
    private val articleService: ArticleService,
    private val mapper: ObjectMapper,
) {

    fun sell(productNumber: String) = productRepository.findById(productNumber)
        .switchIfEmpty { productNotFoundError(productNumber) }
        .filterWhen { productComposer.isProductSellable(it) }
        .switchIfEmpty { productNotSellableError(productNumber) }
        .flatMapMany { confirmSell(it) }
        .then()

    fun getAll(page: Int, size: Int) = productComposer.findAllProductsPaged(of(page, size))

    fun saveFile(file: Flux<FilePart>): Mono<Void> {
        return readFile(file)
            .map { parseFromJson(it) }
            .onErrorResume { error(BadRequestException("invalid json file, please check! error=${it.message}")) }
            .flatMapMany { productRepository.saveAll(it) }
            .then()
    }

    private fun parseFromJson(payload: String) =
        mapper.readValue<Map<String, List<ProductRequest>>>(payload)
            .values.flatten().map { it.toDomain() }

    private fun confirmSell(product: Product) = articleService.subtractInventory(product)

    private fun productNotSellableError(productNumber: String): Mono<Product> = error(UnprocessableEntityException(
        "productNumber=$productNumber is not sellable because one or more of its articles doesn't have enough stock!"
    ))

    private fun productNotFoundError(productNumber: String): Mono<Product> = error(NotFoundException(
        "productNumber=$productNumber not found!"
    ))
}
