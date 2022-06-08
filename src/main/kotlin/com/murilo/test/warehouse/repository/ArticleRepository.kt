package com.murilo.test.warehouse.repository

import com.murilo.test.warehouse.domain.Article
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository : ReactiveMongoRepository<Article, Long>