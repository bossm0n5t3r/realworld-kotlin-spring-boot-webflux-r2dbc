package com.realworld.exception

import org.springframework.http.HttpStatus

class InvalidRequestException(
    errors: Map<String, List<String>>,
) : CommonException(HttpStatus.UNPROCESSABLE_ENTITY, errors) {
    constructor(errorCode: ErrorCode) : this(mapOf(errorCode.target.name to listOf(errorCode.action.name)))
}
