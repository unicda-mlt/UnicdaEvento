package com.database.entities

import androidx.room.*

@Entity(
    tableName = "students",
    indices = [Index(value = ["registration_id"], unique = true)]
)
data class Student(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "registration_id") val registrationId: String,
    @ColumnInfo(name = "password") val password: String,
)
