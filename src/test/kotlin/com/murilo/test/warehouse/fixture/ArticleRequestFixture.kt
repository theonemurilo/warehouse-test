package com.murilo.test.warehouse.fixture

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.murilo.test.warehouse.controller.request.ArticleRequest

private val mapper = jacksonObjectMapper()

fun getArticleRequest() = ArticleRequest(
    articleId = 1,
    name = "dummy article 1",
    stock = 10,
    minStock = 5
)

fun getJsonMapObject(articleRequest: ArticleRequest) = mapOf("inventory" to listOf(articleRequest))

fun getInventoryJsonPayload(): String = mapper.writeValueAsString(getJsonMapObject(getArticleRequest()))