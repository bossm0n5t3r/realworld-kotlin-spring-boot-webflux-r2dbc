package com.realworld.exception

data class InvalidRequestExceptionResponse(val errors: Map<String, List<String>>)
