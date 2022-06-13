package com.murilo.test.warehouse.utils

import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

fun readFile(file: Flux<FilePart>): Mono<String> {
    return file.flatMap { it.content() }
        .map { it.asInputStream().bufferedReader().readText() }
        .collectList()
        .map { it.joinToString("") }
}
