package com.database.dao

import androidx.room.*
import com.database.entities.Event
import com.database.entities.EventWithRefs
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("""
        SELECT * FROM events e
        WHERE (:title IS NULL OR e.title LIKE :title ESCAPE '\')
          AND (:fromDate IS NULL OR e.start_date >= :fromDate)
          AND (:toDate IS NULL OR e.end_date <= :toDate)
          AND (:departmentId IS NULL OR e.department_id = :departmentId)
          AND (:categoryEventId IS NULL OR e.event_category_id = :categoryEventId)
        ORDER BY e.start_date
    """)
    fun observeAll(
        title: String? = null,
        fromDate: Long? = null,
        toDate: Long? = null,
        departmentId: Long? = null,
        categoryEventId: Long? = null
    ): Flow<List<Event>>

    @Transaction
    @Query("SELECT * FROM events WHERE id = :id")
    fun observeWithRefs(id: Long): Flow<EventWithRefs?>

    @Transaction
    @Query("""
        SELECT * FROM events 
        WHERE (:after IS NULL OR start_date >= :after) 
          AND (:before IS NULL OR end_date <= :before)
        ORDER BY start_date
    """)
    fun observeRange(after: Long?, before: Long?): Flow<List<EventWithRefs>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg e: Event): List<Long>

    @Update suspend fun update(vararg e: Event)
    @Delete suspend fun delete(vararg e: Event)

    @Query("DELETE FROM events")
    suspend fun clear()
}