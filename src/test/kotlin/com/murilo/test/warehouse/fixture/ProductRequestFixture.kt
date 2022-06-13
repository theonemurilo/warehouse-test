package com.murilo.test.warehouse.fixture

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.murilo.test.warehouse.controller.request.ProductArticlesRequest
import com.murilo.test.warehouse.controller.request.ProductRequest
import java.math.BigDecimal

private val mapper = jacksonObjectMapper()

fun getProductRequest() = ProductRequest(
    name = "dummy product 1",
    price = BigDecimal(10.0),
    productArticles = listOf(ProductArticlesRequest(
        articleId = 1,
        amountOf = 10,
    ))
)

fun getJsonMapObject(productRequest: ProductRequest) = mapOf("products" to listOf(productRequest))

fun getJsonPayload(): String = mapper.writeValueAsString(getJsonMapObject(getProductRequest()))