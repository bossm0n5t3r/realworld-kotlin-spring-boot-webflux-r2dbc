package com.realworld.exception

import org.springframework.http.HttpStatus

class InternalServerException(
    errors: Map<String, List<String>>,
) : CommonException(HttpStatus.INTERNAL_SERVER_ERROR, errors) {
    constructor(errorMessage: String) : this(mapOf("errorMessage" to listOf(errorMessage)))
}
