package com.murilo.test.warehouse.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.murilo.test.warehouse.controller.request.ArticleRequest
import com.murilo.test.warehouse.domain.Product
import com.murilo.test.warehouse.exceptions.BadRequestException
import com.murilo.test.warehouse.repository.ArticleCustomRepository
import com.murilo.test.warehouse.repository.ArticleRepository
import com.murilo.test.warehouse.utils.readFile
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.error

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val customRepository: ArticleCustomRepository,
    private val mapper: ObjectMapper,
) {

    fun subtractInventory(product: Product) = customRepository.subtractArticlesFromStockByProduct(product)
    fun saveFile(file: Flux<FilePart>): Mono<Void> {
        return readFile(file)
            .map { parseFromJson(it) }
            .onErrorResume { error(BadRequestException("invalid json file, please check! error=${it.message}")) }
            .flatMapMany { articleRepository.saveAll(it) }
            .then()
    }

    private fun parseFromJson(payload: String) =
        mapper.readValue<Map<String, List<ArticleRequest>>>(payload)
            .values.flatten().map { it.toDomain() }
}