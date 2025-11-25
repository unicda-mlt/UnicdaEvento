package com.domain.entities

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties


@Keep
@IgnoreExtraProperties
data class EventCategory(
    val id: String = "",
    val name: String = "",
    val nameNormalized: String = "",
)
