package com.database.entities

import androidx.room.*
import java.time.Instant

@Entity(
    tableName = "departments",
    indices = [Index(value = ["name"], unique = true)]
)
data class Department(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
)
