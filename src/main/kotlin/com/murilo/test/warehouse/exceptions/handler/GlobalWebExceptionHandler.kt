package com.murilo.test.warehouse.exceptions.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.murilo.test.warehouse.controller.response.ErrorResponse
import com.murilo.test.warehouse.exceptions.BaseException
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
@Order(-2)
class GlobalWebExceptionHandler(
    private val mapper: ObjectMapper,
) : ErrorWebExceptionHandler {

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        val response = when (ex) {
            is BaseException -> buildExpectedMessage(exchange, ex)
            else -> buildNonExpectedExceptionMessage(exchange, ex)
        }

        val bytes = mapper.writeValueAsBytes(response)
        val buffer = exchange.response.bufferFactory().wrap(bytes)
        return exchange.response.writeWith(Mono.just(buffer))
    }

    private fun buildNonExpectedExceptionMessage(exchange: ServerWebExchange, ex: Throwable): ErrorResponse {
        exchange.response.headers.contentType = MediaType.APPLICATION_PROBLEM_JSON
        exchange.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
        return ErrorResponse(ex.message!!, exchange.response.statusCode!!.value())
    }

    private fun buildExpectedMessage(exchange: ServerWebExchange, baseException: BaseException): ErrorResponse {
        exchange.response.headers.contentType = MediaType.APPLICATION_PROBLEM_JSON
        exchange.response.statusCode = baseException.javaClass.getAnnotation(ResponseStatus::class.java).code
        return ErrorResponse(baseException.message!!, exchange.response.statusCode!!.value())
    }
}