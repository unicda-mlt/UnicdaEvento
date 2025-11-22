package com.domain.entities

enum class Entity(val collection: String) {
    DEPARTMENT("departments"),
    EVENT_CATEGORY("event_categories"),
    EVENT("events"),
    USER_EVENT("user_events"),
    USER_ROLE("user_roles"),
    USER_WITH_ROLE("user_with_roles")
}