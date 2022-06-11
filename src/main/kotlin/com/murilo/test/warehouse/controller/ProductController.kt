package com.murilo.test.warehouse.controller

import com.murilo.test.warehouse.service.ProductService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService,
) {

    @PostMapping("/productNumber")
    fun sell(@PathVariable("productNumber") productNumber: Long): Mono<Void> = productService.sell(productNumber)

    @GetMapping
    fun getAll(@RequestParam(defaultValue = "0") page: Int, @RequestParam(defaultValue = "3") size: Int) =
        productService.getAll(page, size)

    fun uploadProductsFile() {
        //TODO
    }

}