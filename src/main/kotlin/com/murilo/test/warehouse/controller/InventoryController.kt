package com.murilo.test.warehouse.controller

import com.murilo.test.warehouse.service.ArticleService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/inventories")
class InventoryController(
    private val articleService: ArticleService,
) {

   fun uploadInventoryFile() {
       //TODO
   }
}