package com.realworld.common

fun String.toSlug() = this
    .lowercase()
    .replace("\\s+".toRegex(), "-")
    .replace("-+".toRegex(), "-")
