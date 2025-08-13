package com.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Upsert
import com.database.entities.EventStudentCrossRef

@Dao
interface EventStudentDao {
    @Upsert
    suspend fun upsertAll(refs: List<EventStudentCrossRef>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg e: EventStudentCrossRef)
}
