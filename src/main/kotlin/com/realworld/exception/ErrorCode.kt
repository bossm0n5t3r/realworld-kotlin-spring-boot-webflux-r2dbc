package com.realworld.exception

enum class ErrorCode(
    val target: ErrorTarget,
    val action: ErrorAction,
) {
    EMAIL_ALREADY_EXISTS(ErrorTarget.EMAIL, ErrorAction.ALREADY_EXISTS),
    USERNAME_ALREADY_EXISTS(ErrorTarget.USERNAME, ErrorAction.ALREADY_EXISTS),
    USER_NOT_FOUND(ErrorTarget.USER, ErrorAction.NOT_FOUND),
    USERNAME_NOT_FOUND(ErrorTarget.USERNAME, ErrorAction.NOT_FOUND),
    TOKEN_INVALID(ErrorTarget.TOKEN, ErrorAction.INVALID),
    AUTHORIZATION_HEADER_HAS_NO_TOKEN_PREFIX(ErrorTarget.AUTHORIZATION_HEADER, ErrorAction.HAS_NO_TOKEN_PREFIX),
    ARTICLE_NOT_FOUND(ErrorTarget.ARTICLE, ErrorAction.NOT_FOUND),
    USER_NOT_MATCHED_AUTHOR(ErrorTarget.USER, ErrorAction.NOT_MATCHED_AUTHOR),
}
