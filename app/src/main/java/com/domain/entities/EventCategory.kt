package com.domain.entities

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import com.util.normalizeText


@Keep
@IgnoreExtraProperties
data class EventCategory(
    val id: String = "",
    val name: String = "",
    val nameNormalized: String = "",
) {
    companion object {
        fun create(
            id: String = "",
            name: String,
        ): EventCategory {
            return EventCategory(
                id = id,
                name = name,
                nameNormalized = normalizeText(name)
            )
        }
    }
}
