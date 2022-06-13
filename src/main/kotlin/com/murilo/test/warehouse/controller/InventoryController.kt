package com.murilo.test.warehouse.controller

import com.murilo.test.warehouse.service.ArticleService
import org.springframework.http.HttpStatus
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/inventories")
class InventoryController(
    private val articleService: ArticleService,
) {
    @PostMapping(params = ["uploadFile"])
    @ResponseStatus(HttpStatus.CREATED)
    fun uploadInventoryFile(@RequestPart(name = "file") file: Flux<FilePart>) = articleService.saveFile(file)
}