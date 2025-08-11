package com.database.dao

import androidx.room.*
import com.database.entities.EventCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface EventCategoryDao {
    @Query("SELECT * FROM event_categories ORDER BY name")
    fun observeAll(): Flow<List<EventCategory>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg c: EventCategory): List<Long>

    @Update suspend fun update(vararg c: EventCategory)
    @Delete suspend fun delete(vararg c: EventCategory)
}