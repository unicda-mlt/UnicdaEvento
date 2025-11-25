package com.domain.entities

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import com.util.normalizeText


@Keep
@IgnoreExtraProperties
data class Department(
    val id: String = "",
    val name: String = "",
    val nameNormalized: String = "",
) {
    companion object {
        fun create(
            id: String = "",
            name: String,
        ): Department {
            return Department(
                id = id,
                name = name,
                nameNormalized = normalizeText(name)
            )
        }
    }
}
