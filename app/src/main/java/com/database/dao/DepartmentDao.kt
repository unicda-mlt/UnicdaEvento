package com.database.dao

import androidx.room.*
import com.database.entities.Department
import kotlinx.coroutines.flow.Flow

@Dao
interface DepartmentDao {
    @Query("SELECT * FROM departments ORDER BY name")
    fun observeAll(): Flow<List<Department>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg d: Department): List<Long>

    @Update suspend fun update(vararg d: Department)
    @Delete suspend fun delete(vararg d: Department)
}