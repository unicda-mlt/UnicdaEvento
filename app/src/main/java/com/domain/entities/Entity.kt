package com.domain.entities

enum class Entity(val collection: String) {
    DEPARTMENT("departments"),
    EVENT_CATEGORY("event_categories"),
    EVENT("events"),
    USER_EVENT("user_events")
}