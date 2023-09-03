package com.realworld.exception

import org.springframework.http.HttpStatus

open class CommonException(
    val httpStatus: HttpStatus,
    val errors: Map<String, List<String>>,
) : RuntimeException()
