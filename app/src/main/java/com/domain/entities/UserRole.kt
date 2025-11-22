package com.domain.entities

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties


enum class UserRole(val value: String) {
    NO_ROLE("no_role"),
    ADMIN("admin");

    companion object {
        private val map = entries.associateBy(UserRole::value)

        fun fromValue(value: String?): UserRole =
            map[value] ?: NO_ROLE
    }
}

@Keep
@IgnoreExtraProperties
data class UserRoleEntity(
    val id: String = "",
    val name: String = "",
)
