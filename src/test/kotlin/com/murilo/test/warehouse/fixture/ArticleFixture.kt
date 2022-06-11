package com.murilo.test.warehouse.fixture

import com.murilo.test.warehouse.domain.Article

fun getArticle(articleId: Long = 1, stock: Int = 10, minStock: Int = 5) = Article(
    articleId = articleId,
    name = "dummy article $articleId",
    stock = stock,
    minStock = minStock,
)

fun getArticles(articleId: Long = 2, stock: Int = 10, minStock: Int = 5) = listOf(
    Article(
        articleId = articleId,
        name = "dummy article $articleId",
        stock = stock,
        minStock = minStock,
    ), Article(
        articleId = articleId + 1,
        name = "dummy article ${articleId + 1}",
        stock = stock,
        minStock = minStock,
    )
)