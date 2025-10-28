package com.domain.entities

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties


@Keep
@IgnoreExtraProperties
data class Department(
    val id: String = "",
    val name: String = "",
)
