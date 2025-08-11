package com.database.entities

import androidx.room.*

data class EventWithRefs(
    @Embedded val event: Event,
    @Relation(parentColumn = "department_id", entityColumn = "id")
    val department: Department,
    @Relation(parentColumn = "event_category_id", entityColumn = "id")
    val category: EventCategory
)