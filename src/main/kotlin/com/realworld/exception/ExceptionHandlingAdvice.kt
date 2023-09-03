package com.realworld.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException

@RestControllerAdvice
class ExceptionHandlingAdvice {
    @ExceptionHandler(CommonException::class)
    fun commonExceptionHandler(e: CommonException): ResponseEntity<InvalidRequestExceptionResponse> {
        return ResponseEntity(InvalidRequestExceptionResponse(e.errors), e.httpStatus)
    }

    @ExceptionHandler(WebExchangeBindException::class)
    fun constraintViolationExceptionHandler(ex: WebExchangeBindException): InvalidRequestExceptionResponse {
        val responseBody = mutableMapOf<String, MutableList<String>>()
        for (fieldError in ex.fieldErrors) {
            val errors = responseBody.getOrPut(fieldError.field) { mutableListOf() }
            errors.add(fieldError.defaultMessage ?: "")
        }
        return InvalidRequestExceptionResponse(responseBody)
    }
}

data class InvalidRequestExceptionResponse(val errors: Map<String, List<String>>)
