package com.realworld.exception

import org.springframework.http.HttpStatus

class ValidationException(
    errors: Map<String, List<String>>,
) : CommonException(HttpStatus.UNPROCESSABLE_ENTITY, errors) {
    constructor(subject: String, violation: String) : this(mapOf(subject to listOf(violation)))
}
