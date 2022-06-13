package com.murilo.test.warehouse.controller

import com.murilo.test.warehouse.service.ProductService
import org.springframework.http.HttpStatus
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService,
) {

    @PostMapping("/productNumber")
    fun sell(@PathVariable("productNumber") productNumber: String): Mono<Void> = productService.sell(productNumber)

    @GetMapping
    fun getAll(@RequestParam(defaultValue = "0") page: Int, @RequestParam(defaultValue = "3") size: Int) =
        productService.getAll(page, size)

    @PostMapping(params = ["uploadFile"])
    @ResponseStatus(HttpStatus.CREATED)
    fun uploadProductsFile(@RequestPart(name = "file") file: Flux<FilePart>) = productService.saveFile(file)

}