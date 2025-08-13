package com.database.entities

import androidx.room.*

@Entity(
    tableName = "events",
    foreignKeys = [
        ForeignKey(
            entity = Department::class,
            parentColumns = ["id"],
            childColumns = ["department_id"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = EventCategory::class,
            parentColumns = ["id"],
            childColumns = ["event_category_id"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["department_id"]),
        Index(value = ["event_category_id"]),
        Index(value = ["start_date"]),
        Index(value = ["end_date"])
    ]
)
data class Event(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "department_id") val departmentId: Long,
    @ColumnInfo(name = "event_category_id") val eventCategoryId: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "start_date") val startDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "end_date") val endDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "principal_image") val principalImage: String?,
)
