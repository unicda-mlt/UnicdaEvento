package com.database.entities

import androidx.room.*

@Entity(
    tableName = "event_categories",
    indices = [Index(value = ["name"], unique = true)]
)
data class EventCategory(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
)
