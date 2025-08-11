package com.database.dao

import androidx.room.*
import com.database.entities.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY last_name, name")
    fun observeAll(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id")
    fun observeById(id: Long): Flow<Student?>

    @Query("SELECT * FROM students WHERE registration_id = :regId LIMIT 1")
    suspend fun findByRegistrationId(regId: String): Student?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg s: Student): List<Long>

    @Update
    suspend fun update(vararg s: Student)

    @Delete
    suspend fun delete(vararg s: Student)

    @Query("DELETE FROM students")
    suspend fun clear()
}
