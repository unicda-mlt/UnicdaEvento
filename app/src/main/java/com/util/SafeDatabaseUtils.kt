package com.util

fun likeWrap(q: String?): String? =
    q?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?.replace("\\", "\\\\")
        ?.replace("%", "\\%")
        ?.replace("_", "\\_")
        ?.let { "%$it%" }
