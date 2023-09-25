package com.realworld.exception

import jakarta.annotation.Priority
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.http.HttpStatus
import org.springframework.http.codec.HttpMessageWriter
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.reactive.function.server.HandlerStrategies
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.result.view.ViewResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@ControllerAdvice
@Priority(0) // should go before WebFluxResponseStatusExceptionHandler
class CustomWebExceptionHandler : ErrorWebExceptionHandler {
    override fun handle(exchange: ServerWebExchange, e: Throwable): Mono<Void> {
        return when (e) {
            is CommonException -> handleCommonException(e)
            is WebExchangeBindException -> handleWebExchangeBindException(e)
            else -> error(e)
        }.flatMap { it.writeTo(exchange, ResponseContextInstance) }.then()
    }

    private fun handleCommonException(e: CommonException): Mono<ServerResponse> {
        return ServerResponse
            .status(e.httpStatus)
            .bodyValue(InvalidRequestExceptionResponse(e.errors))
    }

    private fun handleWebExchangeBindException(ex: WebExchangeBindException): Mono<ServerResponse> {
        val responseBody = mutableMapOf<String, MutableList<String>>()
        for (fieldError in ex.fieldErrors) {
            val errors = responseBody.getOrPut(fieldError.field) { mutableListOf() }
            errors.add(fieldError.defaultMessage ?: "")
        }
        return ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(InvalidRequestExceptionResponse(responseBody))
    }

    private object ResponseContextInstance : ServerResponse.Context {

        val strategies: HandlerStrategies = HandlerStrategies.withDefaults()

        override fun messageWriters(): List<HttpMessageWriter<*>> {
            return strategies.messageWriters()
        }

        override fun viewResolvers(): List<ViewResolver> {
            return strategies.viewResolvers()
        }
    }
}
